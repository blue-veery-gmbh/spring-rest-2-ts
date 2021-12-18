package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;

public class JacksonAnnotationsConversionToJacksonJs extends TypeBasedConversionToJacksonJs {

    protected final TSFunction jsonTypeInfoFunction;
    protected final TSEnum jsonTypeInfoIdEnum;
    protected final TSEnum JsonTypeInfoAs;

    public JacksonAnnotationsConversionToJacksonJs() {
        jsonTypeInfoFunction = new TSFunction("JsonTypeInfo", jacksonJSModule);

        jsonTypeInfoIdEnum = new TSEnum("JsonTypeInfoId", jacksonJSModule);
        jsonTypeInfoIdEnum.add("NAME");

        JsonTypeInfoAs = new TSEnum("JsonTypeInfoAs", jacksonJSModule);
    }

    @Override
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        TSClass tsClass = (TSClass) tsScopedElement;
        super.tsScopedTypeCreated(javaType, tsClass);

        JsonTypeInfo jsonTypeInfo = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
        if (jsonTypeInfo != null) {
            addJsonTypeInfoAnnotation(tsClass, jsonTypeInfo);
        }
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

        tsClass.getTsDecoratorList().add(jsonTypeInfoDecorator);
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
