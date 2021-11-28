package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.converters.tests.model.Keyboard;
import com.blueveery.springrest2ts.converters.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.converters.tests.model.Product;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelClassesToTsClassesTest {

    private Rest2tsGenerator tsGenerator;
    private JacksonObjectMapper objectMapper;
    private Set<String> javaPackageSet;
    private ModelClassesAbstractConverter modelClassesConverter;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(
                new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class, KeyboardInterface.class)
        );
        objectMapper = new JacksonObjectMapper();
        modelClassesConverter = new ModelClassesToTsAngular2JsonApiConverter(objectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.converters.tests.model");
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

    @Test
    public void productJavaClassIsConvertedToClass() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, "Product");
        assertThat(product).isInstanceOf(TSClass.class);
    }

    @Test
    public void keyboardJavaClassIsConvertedToClass() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement keyboard = findTSComplexElement(tsModules, "Keyboard");
        assertThat(keyboard).isInstanceOf(TSClass.class);
    }

    @Test
    public void javaInheritanceIsMappedToTsInheritance() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, "Keyboard");
        TSComplexElement extendedKeyboard = findTSComplexElement(tsModules, "ExtendedKeyboard");
        assertThat(extendedKeyboard).isInstanceOf(TSClass.class);
        assertThat(((TSClass) extendedKeyboard).getExtendsClass().getReferencedType()).isEqualTo(keyboard);
    }

    @Test
    public void javaInterfaceIsMappedToTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement keyboardInterface = findTSComplexElement(tsModules, "KeyboardInterface");
        assertThat(keyboardInterface).isInstanceOf(TSInterface.class);
    }

    @Test
    public void tsClassImplementsTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, "Keyboard");
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
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, "Keyboard");
        assertThat(keyboard.getImplementsInterfaces()).isEmpty();
    }

    @Test
    public void ifObjectMapperGeneratesFieldsFromGettersTsClassHasOnlySuchFields() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, "Product");
        assertThat(product.getTsFields()).isEmpty();

        TSComplexElement Keyboard = findTSComplexElement(tsModules, "Keyboard");
        assertThat(Keyboard.getTsFields().stream().map(TSField::getName)).containsExactly("keyNumber");
    }

    @Test
    public void ifObjectMapperGeneratesFieldsFromJavaFieldsTsClassHasOnlySuchFields() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, "Product");
        assertThat(product.getTsFields().stream().map(TSField::getName)).containsExactly(
                "intWrapperField", "name", "nullableField", "productionYear"
        );

        TSComplexElement Keyboard = findTSComplexElement(tsModules, "Keyboard");
        assertThat(Keyboard.getTsFields().stream().map(TSField::getName)).containsExactly("fKeyNumber");
    }

    @Test
    public void nullableFieldShouldBeNullableInTS() throws IOException {
        objectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        objectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement product = findTSComplexElement(tsModules, "Product");

        Optional<TSField> nullableField = product.getTsFields()
                .stream().filter(f -> f.getName().equals("nullableField")).findFirst();
        assertThat(nullableField.get().getType()).isInstanceOf(TSUnion.class);
        assertThat(((TSUnion) nullableField.get().getType()).getJoinedTsElementList()).contains(TypeMapper.tsNumber, TypeMapper.tsNull);

        Optional<TSField> intWrapperField = product.getTsFields()
                .stream().filter(f -> f.getName().equals("intWrapperField")).findFirst();
        assertThat(intWrapperField.get().getType()).isInstanceOf(TSUnion.class);
        assertThat(((TSUnion) nullableField.get().getType()).getJoinedTsElementList()).contains(TypeMapper.tsNumber, TypeMapper.tsNull);
    }

    private TSComplexElement findTSComplexElement(SortedSet<TSModule> tsModules, String name) {
        return (TSComplexElement) tsModules.first().
                getScopedTypesSet()
                .stream()
                .filter(t -> name.equals(t.getName()))
                .findFirst().get();
    }
}