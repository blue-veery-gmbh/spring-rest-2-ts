package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tests.BaseTest;
import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelClassesToTsClassesConverterTest extends BaseTest<JacksonObjectMapper> {

    @Override
    protected JacksonObjectMapper createObjectMapper() {
        return new JacksonObjectMapper();
    }

    @Test
    public void productJavaClassIsConvertedToClass() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, Product.class.getSimpleName());
        assertThat(product).isInstanceOf(TSClass.class);
    }

    @Test
    public void keyboardJavaClassIsConvertedToClass() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement keyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard).isInstanceOf(TSClass.class);
    }

    @Test
    public void tsClassesHaveCorrectInheritanceHierarchy() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard.getExtendsClass()).isNull();

        TSClass extendedKeyboard = (TSClass) findTSComplexElement(tsModules, ExtendedKeyboard.class.getSimpleName());
        assertThat(extendedKeyboard.getExtendsClass().getName()).isEqualTo("Keyboard");
    }

    @Test
    public void javaInheritanceIsMappedToTsInheritance() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        TSComplexElement extendedKeyboard = findTSComplexElement(tsModules, ExtendedKeyboard.class.getSimpleName());
        assertThat(extendedKeyboard).isInstanceOf(TSClass.class);
        assertThat(((TSClass) extendedKeyboard).getExtendsClass().getReferencedType()).isEqualTo(keyboard);
    }

    @Test
    public void javaInterfaceIsMappedToTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement keyboardInterface = findTSComplexElement(tsModules, KeyboardInterface.class.getSimpleName());
        assertThat(keyboardInterface).isInstanceOf(TSInterface.class);
    }

    @Test
    public void tsClassImplementsTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        TSInterfaceReference keyboardInterface = (TSInterfaceReference) TypeMapper.map(KeyboardInterface.class);
        assertThat(keyboard.getImplementsInterfaces()).containsExactly(keyboardInterface);
    }

    @Test
    public void tsInterfaceHasFieldsGeneratedFromGetters() throws IOException {
        tsGenerator.convert(javaPackageSet);
        TSInterfaceReference keyboardInterface = (TSInterfaceReference) TypeMapper.map(KeyboardInterface.class);
        assertThat(keyboardInterface.getReferencedType().getTsFields().stream().map(TSField::getName)).containsExactly("keyNumber");
    }

    @Test
    public void ifInterfacesAreExcludedFromGenerationSetTsClassesDontImplementThem() throws IOException {
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard.getImplementsInterfaces()).isEmpty();
    }

    @Test
    public void ifObjectMapperGeneratesFieldsFromGettersTsClassHasOnlySuchFields() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, Product.class.getSimpleName());
        assertThat(product.getTsFields()).isEmpty();

        TSComplexElement Keyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(Keyboard.getTsFields().stream().map(TSField::getName)).containsExactly("keyNumber");
    }

    @Test
    public void ifObjectMapperGeneratesFieldsFromJavaFieldsTsClassHasOnlySuchFields() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, Product.class.getSimpleName());
        assertThat(product.getTsFields().stream().map(TSField::getName)).containsExactlyInAnyOrder(
                "intWrapperField", "name", "nullableField", "productionYear", "extendedKeyboards", "keyboard"
        );

        TSComplexElement Keyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(Keyboard.getTsFields().stream().map(TSField::getName)).containsExactly("fKeyNumber");
    }

    @Test
    public void nullableFieldShouldBeNullableInTS() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, Product.class.getSimpleName());

        Optional<TSField> nullableField = product.getTsFields()
                .stream().filter(f -> f.getName().equals("nullableField")).findFirst();
        assertThat(nullableField.get().getType()).isInstanceOf(TSUnion.class);
        assertThat(((TSUnion) nullableField.get().getType()).getJoinedTsElementList()).contains(TypeMapper.tsNumber, TypeMapper.tsNull);

        Optional<TSField> intWrapperField = product.getTsFields()
                .stream().filter(f -> f.getName().equals("intWrapperField")).findFirst();
        assertThat(intWrapperField.get().getType()).isInstanceOf(TSUnion.class);
        assertThat(((TSUnion) nullableField.get().getType()).getJoinedTsElementList()).contains(TypeMapper.tsNumber, TypeMapper.tsNull);
    }
}