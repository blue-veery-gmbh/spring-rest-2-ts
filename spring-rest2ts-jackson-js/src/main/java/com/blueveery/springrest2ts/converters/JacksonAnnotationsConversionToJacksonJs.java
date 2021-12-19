package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiFunction;

public class JacksonAnnotationsConversionToJacksonJs extends TypeBasedConversionToJacksonJs {

    protected final TSFunction jsonTypeInfoFunction;
    protected final TSFunction jsonSubTypesFunction;
    protected final TSEnum jsonTypeInfoIdEnum;
    protected final TSEnum JsonTypeInfoAs;

    protected BiFunction<Class, Class, String> typeIdResolver = (Class type, Class rootType) -> {
        throw new IllegalStateException("When Id.NAME is used you need to set typeIdResolver!");
    };

    public JacksonAnnotationsConversionToJacksonJs(BiFunction<Class, Class, String> typeIdResolver) {
        this();
        this.typeIdResolver = typeIdResolver;
    }

    public JacksonAnnotationsConversionToJacksonJs() {
        jsonTypeInfoFunction = new TSFunction("JsonTypeInfo", jacksonJSModule);
        jsonSubTypesFunction = new TSFunction("JsonSubTypes", jacksonJSModule);;

        jsonTypeInfoIdEnum = new TSEnum("JsonTypeInfoId", jacksonJSModule);
        JsonTypeInfoAs = new TSEnum("JsonTypeInfoAs", jacksonJSModule);

    }

    public BiFunction<Class, Class, String> getTypeIdResolver() {
        return typeIdResolver;
    }

    public void setTypeIdResolver(BiFunction<Class, Class, String> typeIdResolver) {
        this.typeIdResolver = typeIdResolver;
    }

    @Override
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        if (tsScopedElement instanceof TSClass) {
            TSClass tsClass = (TSClass) tsScopedElement;
            super.tsScopedTypeCreated(javaType, tsClass);

            JsonTypeInfo jsonTypeInfo = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
            if (jsonTypeInfo != null) {
                addJsonTypeInfoAnnotation(tsClass, jsonTypeInfo);
                getOrCreateJsonSubTypes(tsClass);
                if (!Modifier.isAbstract(javaType.getModifiers())) {
                    addTypeToJsonSubTypes(javaType, javaType, tsClass);
                }
            } else {
                tryToAddTypeToJsonSubTypes(javaType, tsClass);
            }
        }
    }

    private void tryToAddTypeToJsonSubTypes(Class javaType, TSClass tsClass) {
        if (!Modifier.isAbstract(javaType.getModifiers())) {
            Class javaRoot = findInheritanceRoot(javaType);
            if (javaRoot != null) {
                addTypeToJsonSubTypes(javaType, javaRoot, tsClass);
            }
        }
    }

    private void addTypeToJsonSubTypes(Class javaType, Class javaRoot, TSClass tsClass) {
        TSClass tsRoot = ((TSClassReference) TypeMapper.map(javaRoot)).getReferencedType();
        TSDecorator jsonSubTypes = getOrCreateJsonSubTypes(tsRoot);
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonSubTypes.getTsLiteralList().get(0);
        TSLiteralArray types = (TSLiteralArray) jsonLiteral.getFieldMap().get("types");
        TSJsonLiteral typeMapping = new TSJsonLiteral();
        typeMapping.getFieldMap().put("class", new TSArrowFunctionLiteral(new TSTypeLiteral(tsClass)));
        typeMapping.getFieldMap().put("name", getTypeName(javaType, javaRoot));
        types.getLiteralList().add(typeMapping);
    }

    private TSDecorator getOrCreateJsonSubTypes(TSClass tsRoot) {
        return tsRoot.getTsDecoratorList().stream()
                .filter(d -> d.getTsFunction() == jsonSubTypesFunction).findFirst().orElseGet(() ->addJsonSubTypesAnnotation(tsRoot));
    }

    private ILiteral getTypeName(Class javaType, Class javaRoot) {
        JsonTypeInfo jsonTypeInfo = (JsonTypeInfo) javaRoot.getAnnotation(JsonTypeInfo.class);
        switch (jsonTypeInfo.use()) {
            case CLASS:
                return new TSLiteral("", TypeMapper.tsString, javaType.getName());
            case MINIMAL_CLASS:
                return new TSLiteral("", TypeMapper.tsString, getMinimalClassName(javaType, javaRoot));
            case NAME:
                return new TSLiteral("", TypeMapper.tsString, typeIdResolver.apply(javaType, javaRoot));
            default:
                throw new IllegalStateException(jsonTypeInfo.use() + " is not supported");
        }
    }

    private String getMinimalClassName(Class javaType, Class javaRoot) {
        if ( javaType.getName().startsWith(javaRoot.getPackage().getName())) {
            return javaType.getName().replaceFirst(javaRoot.getPackage().getName(), ".");
        }
        return javaType.getName();
    }

    private Class findInheritanceRoot(Class javaType) {
        Class superclass = javaType.getSuperclass();
        if (superclass != null && superclass != Object.class && TypeMapper.map(superclass) != TypeMapper.tsAny) {
            if (superclass.getAnnotation(JsonTypeInfo.class) == null) {
                return findInheritanceRoot(superclass);
            } else {
                return superclass;
            }
        }
        return null;
    }

    private TSDecorator addJsonSubTypesAnnotation(TSClass tsClass) {
        TSDecorator jsonSubTypesFunctionDecorator = new TSDecorator(jsonSubTypesFunction);
        TSJsonLiteral literal = new TSJsonLiteral();
        literal.getFieldMap().put("types", new TSLiteralArray());
        jsonSubTypesFunctionDecorator.getTsLiteralList().add(literal);
        tsClass.getTsDecoratorList().add(jsonSubTypesFunctionDecorator);
        tsClass.addScopedTypeUsage(jsonSubTypesFunction);
        return jsonSubTypesFunctionDecorator;
    }

    private void addJsonTypeInfoAnnotation(TSClass tsClass, JsonTypeInfo jsonTypeInfo) {
        TSDecorator jsonTypeInfoDecorator = new TSDecorator(jsonTypeInfoFunction);
        TSJsonLiteral jsonTypeInfoLiteral = new TSJsonLiteral();
        jsonTypeInfoDecorator.getTsLiteralList().add(jsonTypeInfoLiteral);
        tsClass.addScopedTypeUsage(jsonTypeInfoFunction);

        convertJsonTypeInfoUse(tsClass, jsonTypeInfo, jsonTypeInfoLiteral);
        if (convertJsonTypeInfoInclude(tsClass, jsonTypeInfo, jsonTypeInfoLiteral)) {
            convertJsonTypeInfoProperty(jsonTypeInfo, jsonTypeInfoLiteral);
        }

        tsClass.getTsDecoratorList().add(0, jsonTypeInfoDecorator);
    }

    private void convertJsonTypeInfoProperty(JsonTypeInfo jsonTypeInfo, TSJsonLiteral jsonTypeInfoLiteral) {
        String propertyName = jsonTypeInfo.property();
        if("".equals(propertyName)) {
            propertyName = jsonTypeInfo.use().getDefaultPropertyName();
        }
        if (!"@type".equals(propertyName)) {
            jsonTypeInfoLiteral.getFieldMap().put("property", new TSLiteral("", TypeMapper.tsString, propertyName));
        }
    }

    private void convertJsonTypeInfoUse(TSClass tsClass, JsonTypeInfo jsonTypeInfo, TSJsonLiteral jsonTypeInfoLiteral) {
        if (Arrays.asList(JsonTypeInfo.Id.NAME, JsonTypeInfo.Id.CLASS, JsonTypeInfo.Id.MINIMAL_CLASS).contains(jsonTypeInfo.use())) {
            jsonTypeInfoLiteral.getFieldMap().put("use", new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoId.NAME"));
            tsClass.addScopedTypeUsage(jsonTypeInfoIdEnum);
        } else {
            throw new IllegalStateException("JsonTypeInfo.use()" + jsonTypeInfo.use() + " is not supported in jackson-js");
        }
    }

    private boolean convertJsonTypeInfoInclude(TSClass tsClass, JsonTypeInfo jsonTypeInfo, TSJsonLiteral jsonTypeInfoLiteral) {
        boolean isProperty = false;
        String tsInclude = null;
        switch(jsonTypeInfo.include()) {
            case PROPERTY:
            case EXISTING_PROPERTY:
                tsInclude = "JsonTypeInfoAs.PROPERTY";
                isProperty = true;
                break;
            case WRAPPER_OBJECT:
                tsInclude = "JsonTypeInfoAs.WRAPPER_OBJECT";
                break;
            case WRAPPER_ARRAY:
                tsInclude = "JsonTypeInfoAs.WRAPPER_ARRAY";
                break;
            case EXTERNAL_PROPERTY:
                throw new IllegalStateException("EXTERNAL_PROPERTY is not supported in jackson-js");

        }
        jsonTypeInfoLiteral.getFieldMap().put("include", new TSLiteral("", TypeMapper.tsAny, tsInclude));
        tsClass.addScopedTypeUsage(JsonTypeInfoAs);
        return isProperty;
    }
}
