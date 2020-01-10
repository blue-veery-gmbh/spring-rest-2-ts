package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by tomaszw on 10.01.2020.
 */
public class ModelClassesToTsAngular2JsonApiConverter extends ModelClassesAbstractConverter {

    private TSModule angular2JsonApiModule;
    private TSFunction jsonApiModelConfigFunction;

    private TSDecorator attributeDecorator;
    private TSDecorator hasManyDecorator;
    private TSDecorator belongsToDecorator;

    private TSClass jsonApiModelClass;
    private TSClassReference tsJsonApiModelClassReference;

    public ModelClassesToTsAngular2JsonApiConverter(ObjectMapper objectMapper) {
        this(new NoChangeClassNameMapper(), objectMapper);
    }

    public ModelClassesToTsAngular2JsonApiConverter(ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper, objectMapper);
        angular2JsonApiModule = new TSModule("angular2-jsonapi", null, true);
        jsonApiModelConfigFunction = new TSFunction("JsonApiModelConfig", angular2JsonApiModule);
        attributeDecorator = new TSDecorator(new TSFunction("Attribute", angular2JsonApiModule));
        hasManyDecorator = new TSDecorator(new TSFunction("HasMany", angular2JsonApiModule));
        belongsToDecorator = new TSDecorator(new TSFunction("BelongsTo", angular2JsonApiModule));
        jsonApiModelClass = new TSClass("JsonApiModel", angular2JsonApiModule, new EmptyImplementationGenerator());
        tsJsonApiModelClassReference = new TSClassReference(jsonApiModelClass, Collections.emptyList());
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                if (javaClass.isInterface()) {
                    TSInterface tsInterface = new TSInterface(createTsClassName(javaClass), tsModule);
                    tsModule.addScopedType(tsInterface);
                    TypeMapper.registerTsType(javaClass, tsInterface);
                }else {
                    TSClass tsClass = new TSClass(createTsClassName(javaClass), tsModule, getImplementationGenerator());
                    tsModule.addScopedType(tsClass);
                    TypeMapper.registerTsType(javaClass, tsClass);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSClassReference tsClassReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsClassReference.getReferencedType();
        if (!tsClass.isConverted()) {
            tsClass.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), tsClassReference);
            if (!javaClass.isInterface()) {
                TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
                TSClassReference tsSuperClassReference;
                if (superClass instanceof TSClassReference) {
                    tsSuperClassReference = (TSClassReference) superClass;
                }else{
                    tsSuperClassReference = tsJsonApiModelClassReference;
                }
                tsClass.setExtendsClass(tsSuperClassReference);
                TSDecorator jsonApiModelConfigDecorator = createJsonApiModelConfigDecorator(tsClass);
                tsClass.getTsDecoratorList().add(jsonApiModelConfigDecorator);
                tsClass.addScopedTypeUsage(jsonApiModelConfigFunction);
            }

            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType superClass = TypeMapper.map(annotatedInterface.getType());
                if (superClass instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
                    tsClass.addImplementsInterfaces(tsSuperClassInterface);
                }
            }


            SortedSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsClass, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsClass.addTsField(tsField);
                    addAngular2JsonApiDecorators(tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            for (TSField typeLevelSpecificField : objectMapper.addTypeLevelSpecificFields(javaClass, tsClass)) {
                addAngular2JsonApiDecorators(typeLevelSpecificField);
                conversionListener.tsFieldCreated(new Property(typeLevelSpecificField.getName(), 0), typeLevelSpecificField);
            }
            tsClass.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsClass);
        }

    }

    private TSDecorator createJsonApiModelConfigDecorator(TSClass tsClass) {
        TSDecorator jsonApiModelConfigDecorator = new TSDecorator(jsonApiModelConfigFunction);
        TSJsonLiteral jsonApiModelConfigParam = new TSJsonLiteral();
        jsonApiModelConfigParam.getFieldMap().put("type", new TSLiteral("", TypeMapper.tsString, tsClass.getName().toLowerCase()+"s"));
        jsonApiModelConfigDecorator.getTsLiteralList().add(jsonApiModelConfigParam);
        return jsonApiModelConfigDecorator;
    }

    private void addAngular2JsonApiDecorators(TSField tsField) {
        if(tsField.getType() instanceof TSArray){
            tsField.getTsDecoratorList().add(hasManyDecorator);
            tsField.getOwner().addScopedTypeUsage(hasManyDecorator.getTsFunction());
            return;
        }

        if(tsField.getType() instanceof TSClassReference){
            TSClassReference tsClassReference = (TSClassReference) tsField.getType();
            if(tsClassReference.getReferencedType().isInstanceOf(jsonApiModelClass)){
                tsField.getTsDecoratorList().add(belongsToDecorator);
                tsField.getOwner().addScopedTypeUsage(belongsToDecorator.getTsFunction());
                return;
            }
        }

        tsField.getTsDecoratorList().add(attributeDecorator);
        tsField.getOwner().addScopedTypeUsage(attributeDecorator.getTsFunction());
    }

}
