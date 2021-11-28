package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.angular2jsonapi.JsonApiModelConfig;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDeclarationType;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSVariable;
import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithFormalTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;

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

    TSVariable modelsVariable;

    public ModelClassesToTsAngular2JsonApiConverter(ObjectMapper objectMapper) {
        this(objectMapper, new NoChangeClassNameMapper());
    }

    public ModelClassesToTsAngular2JsonApiConverter(ObjectMapper objectMapper, ClassNameMapper classNameMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper, objectMapper);
        angular2JsonApiModule = new TSModule("angular2-jsonapi", null, true);
        jsonApiModelConfigFunction = new TSFunction("JsonApiModelConfig", angular2JsonApiModule);
        attributeDecorator = new TSDecorator(new TSFunction("Attribute", angular2JsonApiModule));
        hasManyDecorator = new TSDecorator(new TSFunction("HasMany", angular2JsonApiModule));
        belongsToDecorator = new TSDecorator(new TSFunction("BelongsTo", angular2JsonApiModule));
        jsonApiModelClass = new TSClass("JsonApiModel", angular2JsonApiModule, new EmptyImplementationGenerator());
        tsJsonApiModelClassReference = new TSClassReference(jsonApiModelClass, Collections.emptyList());
    }

    public TSVariable getModelsVariable() {
        return modelsVariable;
    }

    public void setModelsVariable(TSVariable modelsVariable) {
        this.modelsVariable = modelsVariable;
    }

    public void createModelsVariable(String modelsVariableName, TSModule tsModuleForModelsVariable) {
        TSVariable modelsVariable = new TSVariable(modelsVariableName, tsModuleForModelsVariable, TSDeclarationType.CONST, TypeMapper.tsObject, new TSJsonLiteral());
        tsModuleForModelsVariable.addScopedElement(modelsVariable);
        this.modelsVariable = modelsVariable;
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                if (javaClass.isInterface()) {
                    TSInterface tsInterface = new TSInterface(createTsClassName(javaClass), tsModule);
                    tsModule.addScopedElement(tsInterface);
                    TypeMapper.registerTsType(javaClass, tsInterface);
                }else {
                    TSClass tsClass = new TSClass(createTsClassName(javaClass), tsModule, getImplementationGenerator());
                    tsModule.addScopedElement(tsClass);
                    TypeMapper.registerTsType(javaClass, tsClass);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void convertInheritance(Class javaClass) {
        TSType tsType = TypeMapper.map(javaClass);
        if (!javaClass.isInterface()) {
            TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
            TSClassReference tsSuperClassReference;
            if (superClass instanceof TSClassReference) {
                tsSuperClassReference = (TSClassReference) superClass;
            }else{
                tsSuperClassReference = tsJsonApiModelClassReference;
            }
            TSClassReference tsClassReference = (TSClassReference) tsType;
            TSClass tsClass = tsClassReference.getReferencedType();
            tsClass.setExtendsClass(tsSuperClassReference);

            TSDecorator jsonApiModelConfigDecorator = createJsonApiModelConfigDecorator(javaClass, tsClass);
            tsClass.getTsDecoratorList().add(jsonApiModelConfigDecorator);
            tsClass.addScopedTypeUsage(jsonApiModelConfigFunction);

            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType implementedInterface = TypeMapper.map(annotatedInterface.getType());
                if (implementedInterface instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) implementedInterface;
                    tsClass.addImplementsInterfaces(tsSuperClassInterface);
                }
            }
        } else {
            TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) tsType;
            TSInterface tsInterface = tsInterfaceReference.getReferencedType();
            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType extendedInterface = TypeMapper.map(annotatedInterface.getType());
                if (extendedInterface instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) extendedInterface;
                    tsInterface.addExtendsInterfaces(tsSuperClassInterface);
                }
            }
        }
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSParameterizedTypeReference<IParameterizedWithFormalTypes> typeReference = (TSParameterizedTypeReference<IParameterizedWithFormalTypes>) TypeMapper.map(javaClass);
        TSComplexElement tsComplexElement = (TSComplexElement) typeReference.getReferencedType();
        if (!tsComplexElement.isConverted()) {
            tsComplexElement.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), typeReference);
            SortedSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsComplexElement, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsComplexElement.addTsField(tsField);
                    addAngular2JsonApiDecorators(property, tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            for (TSField typeLevelSpecificField : objectMapper.addTypeLevelSpecificFields(javaClass, tsComplexElement)) {
                Property property = new Property(typeLevelSpecificField.getName(), 0);
                addAngular2JsonApiDecorators(property, typeLevelSpecificField);
                conversionListener.tsFieldCreated(property, typeLevelSpecificField);
            }
            tsComplexElement.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsComplexElement);
        }
    }

    private TSDecorator createJsonApiModelConfigDecorator(Class javaClass, TSClass tsClass) {
        TSDecorator jsonApiModelConfigDecorator = new TSDecorator(jsonApiModelConfigFunction);
        TSJsonLiteral jsonApiModelConfigParam = new TSJsonLiteral();

        String typeName = null;
        JsonApiModelConfig jsonApiModelConfig = (JsonApiModelConfig) javaClass.getAnnotation(JsonApiModelConfig.class);
        if (jsonApiModelConfig != null) {
            typeName = jsonApiModelConfig.type();
        }else{
            typeName = tsClass.getName().toLowerCase() + "s";
        }
        jsonApiModelConfigParam.getFieldMap().put("type", new TSLiteral("", TypeMapper.tsString, typeName));
        jsonApiModelConfigDecorator.getTsLiteralList().add(jsonApiModelConfigParam);

        if (modelsVariable != null) {
            TSJsonLiteral models = (TSJsonLiteral) modelsVariable.getValue();
            models.getFieldMap().put(typeName, new TSLiteral("", tsClass, tsClass.getName()));
            modelsVariable.getModule().scopedTypeUsage(tsClass);
        }

        return jsonApiModelConfigDecorator;
    }

    private void addAngular2JsonApiDecorators(Property fromProperty, TSField tsField) {
        if (tsField.getOwner() instanceof TSClass) {
            if (tsField.getType() instanceof TSArray) {
                tsField.getTsDecoratorList().add(hasManyDecorator);
                tsField.getOwner().addScopedTypeUsage(hasManyDecorator.getTsFunction());
                return;
            }

            if (tsField.getType() instanceof TSClassReference) {
                TSClassReference tsClassReference = (TSClassReference) tsField.getType();
                if (tsClassReference.getReferencedType().isInstanceOf(jsonApiModelClass)) {
                    tsField.getTsDecoratorList().add(belongsToDecorator);
                    tsField.getOwner().addScopedTypeUsage(belongsToDecorator.getTsFunction());
                    return;
                }
            }

            tsField.getTsDecoratorList().add(attributeDecorator);
            tsField.getOwner().addScopedTypeUsage(attributeDecorator.getTsFunction());
        }
    }
}
