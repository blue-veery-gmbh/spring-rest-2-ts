package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSVariable;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelClassesToTsAngular2JsonApiConverterTest extends BaseTest {
    @Test
    public void everyModelClassShouldHaveJsonApiModelConfigDecorator() throws IOException {
        TSModule tsModule = tsGenerator.convert(javaPackageSet).first();

        assertThat(tsModule.getScopedTypesSet().stream().filter(e -> e instanceof TSClass).map(e -> (TSClass) e))
                .allMatch(
                        c -> c.getTsDecoratorList().stream()
                                .map(TSDecorator::getTsFunction)
                                .anyMatch(f -> "JsonApiModelConfig".equals(f.getName()))
                );
    }

    @Test
    public void interfacesShouldNotHaveJsonApiModelConfigDecorator() throws IOException {
        TSModule tsModule = tsGenerator.convert(javaPackageSet).first();

        assertThat(tsModule.getScopedTypesSet().stream().filter(e -> e instanceof TSInterface).map(e -> (TSInterface) e))
                .noneMatch(
                        c -> c.getTsDecoratorList().stream()
                                .map(TSDecorator::getTsFunction)
                                .anyMatch(f -> "JsonApiModelConfig".equals(f.getName()))
                );
    }

    @Test
    public void onlyFirstClassInHierarchyExtendsJsonApiModel() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard.getExtendsClass().getName()).isEqualTo("JsonApiModel");

        TSClass extendedKeyboard = (TSClass) findTSComplexElement(tsModules, ExtendedKeyboard.class.getSimpleName());
        assertThat(extendedKeyboard.getExtendsClass().getName()).isEqualTo("Keyboard");
    }

    @Test
    public void simpleClassFieldsShouldHaveAttributeDecorator() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass product = (TSClass) findTSComplexElement(tsModules, Product.class.getSimpleName());
        assertThat(product.getTsFields().stream().filter(f -> f.getType() == TypeMapper.tsNumber || f.getType() == TypeMapper.tsString)).allMatch(
                field -> field.getTsDecoratorList().stream().map(TSDecorator::getTsFunction).anyMatch(f -> "Attribute".equals(f.getName()))
        );
    }

    @Test
    public void interfaceFieldsShouldNotHaveDecorators() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSInterface keyboardInterface = (TSInterface) findTSComplexElement(tsModules, KeyboardInterface.class.getSimpleName());
        assertThat(keyboardInterface.getTsFields().stream()).allMatch(field -> field.getTsDecoratorList().isEmpty());
    }

    @Test
    public void classReferenceFieldShouldHaveBelongsToDecorator() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass product = (TSClass) findTSComplexElement(tsModules, Product.class.getSimpleName());
        TSField keyboardField = product.getTsFields().stream().filter(f -> "keyboard".equals(f.getName())).findFirst().get();
        assertThat(keyboardField.getTsDecoratorList()).anyMatch(d -> "BelongsTo".equals(d.getTsFunction().getName()));
    }

    @Test
    public void modelObjectCollectionFieldShouldHaveHasManyToDecorator() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass product = (TSClass) findTSComplexElement(tsModules, Product.class.getSimpleName());
        TSField extendedKeyboardsField = product.getTsFields().stream().filter(f -> "extendedKeyboards".equals(f.getName())).findFirst().get();
        assertThat(extendedKeyboardsField.getTsDecoratorList()).anyMatch(d -> "HasMany".equals(d.getTsFunction().getName()));
    }

    @Test
    public void modelVariableShouldContainsAllClassMappings() throws IOException {
        JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = tsGenerator.getJavaPackageToTsModuleConverter();
        TSModule tsModuleForModelsVariable = javaPackageToTsModuleConverter.getTsModule(Product.class);
        ModelClassesToTsAngular2JsonApiConverter modelClassesToTsAngular2JsonApiConverter = (ModelClassesToTsAngular2JsonApiConverter) this.modelClassesConverter;
        modelClassesToTsAngular2JsonApiConverter.createModelsVariable("models", tsModuleForModelsVariable);
        TSModule tsModule = tsGenerator.convert(javaPackageSet).first();
        List<String> classNames = tsModule.getScopedTypesSet().stream()
                .filter(e -> e instanceof TSClass)
                .map(c -> ((TSClass) c).getTsDecoratorList().get(0).getTsLiteralList().get(0))
                .map(l -> ((TSJsonLiteral) l).getFieldMap().get("type"))
                .map(v -> ((TSLiteral) v).getValue())
                .collect(Collectors.toList());
        TSVariable modelsVariable = modelClassesToTsAngular2JsonApiConverter.getModelsVariable();
        assertThat(((TSJsonLiteral) modelsVariable.getValue()).getFieldMap().keySet()).containsAll(classNames);

    }
}