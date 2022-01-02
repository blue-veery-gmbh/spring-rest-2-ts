package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.angular2jsonapi.JsonApiModelConfig;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDeclarationType;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSVariable;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;

import java.util.Collections;

/**
 * Created by tomaszw on 10.01.2020.
 */
public class ModelClassesToTsAngular2JsonApiConverter
        extends ModelClassesToTsClassesConverter implements ConversionListener {

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

        conversionListener.getConversionListenerSet().add(this);
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
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        if (tsScopedElement instanceof TSClass) {
            TSClass tsClass = (TSClass) tsScopedElement;
            TSDecorator jsonApiModelConfigDecorator = createJsonApiModelConfigDecorator(javaType, tsClass);
            tsClass.getTsDecoratorList().add(jsonApiModelConfigDecorator);
            tsClass.addScopedTypeUsage(jsonApiModelConfigFunction);
        }
    }

    protected TSClassReference getDefaultBaseClass() {
        return tsJsonApiModelClassReference;
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        addAngular2JsonApiDecorators(property, tsField);
    }

    private TSDecorator createJsonApiModelConfigDecorator(Class javaClass, TSClass tsClass) {
        TSDecorator jsonApiModelConfigDecorator = new TSDecorator(jsonApiModelConfigFunction);
        TSJsonLiteral jsonApiModelConfigParam = new TSJsonLiteral();

        JsonApiModelConfig jsonApiModelConfig = (JsonApiModelConfig) javaClass.getAnnotation(JsonApiModelConfig.class);
        String typeName = jsonApiModelConfig != null ? jsonApiModelConfig.type() : tsClass.getName().toLowerCase() + "s";

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
