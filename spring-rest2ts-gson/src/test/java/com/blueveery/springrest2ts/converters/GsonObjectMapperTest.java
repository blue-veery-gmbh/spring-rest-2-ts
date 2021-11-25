package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Excluder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static org.assertj.core.api.Assertions.assertThat;

public class GsonObjectMapperTest {

    private Rest2tsGenerator tsGenerator;
    private GsonObjectMapper gsonObjectMapper;
    private Set<String> javaPackageSet;
    private ModelClassesAbstractConverter modelClassesConverter;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(new HashSet<>(Arrays.asList(Product.class, Keyboard.class))));
        gsonObjectMapper = new GsonObjectMapper();
        modelClassesConverter = new ModelClassesToTsInterfacesConverter(gsonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.converters");
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

    @Test
    public void testGson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println(gson.toJson(new Product()));
    }

    @Test
    public void transientFieldsAreSkippedInTsCode() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertThat(productTsInterface.getTsFields().stream().noneMatch(f -> "tempName".equals(f.getName()))).isTrue();
    }

    @Test
    public void nonTransientFieldsAreIncludedInTsCode() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertThat(productTsInterface.getTsFields().stream().anyMatch(f -> "name".equals(f.getName()))).isTrue();
    }

    @Test
    public void serializedNameChangesFieldName() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertThat(productTsInterface.getTsFields().stream().anyMatch(f -> "year".equals(f.getName()))).isTrue();
        assertThat(productTsInterface.getTsFields().stream().noneMatch(f -> "productionYear".equals(f.getName()))).isTrue();
    }

    @Test
    public void exposeFiltersFields() throws IOException {
        List<String> exposedFields = Arrays.asList("exposedName", "serializedOnly", "deserializedOnly");
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().allMatch(f -> exposedFields.contains(f.getName()))).isTrue();
        Optional<TSField> exposedNameField = tsFields.stream().filter(f -> "exposedName".equals(f.getName())).findFirst();
        assertThat(TypeMapper.tsString).isEqualTo(exposedNameField.get().getType());
    }

    @Test
    public void serializedOnlyFieldIsReadonly() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> serializedOnlyField = tsFields.stream().filter(f -> "serializedOnly".equals(f.getName())).findFirst();
        assertThat(TypeMapper.tsString).isEqualTo(serializedOnlyField.get().getType());
        assertThat(serializedOnlyField.get().getReadOnly()).isTrue();
    }

    @Test
    public void deserializedOnlyFieldIsOptional() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> deserializedOnlyField = tsFields.stream().filter(f -> "deserializedOnly".equals(f.getName())).findFirst();
        assertThat(deserializedOnlyField.get().getType()).isEqualTo(new TSUnion(TypeMapper.tsUndefined, TypeMapper.tsString));
    }

    @Test
    public void sinceAndForVersionFiltersOutFieldsWithNewerVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(1.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().noneMatch(f -> "sinceField".equals(f.getName()))).isTrue();
    }

    @Test
    public void sinceAndForVersionKeepsFieldsWithOlderVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(2.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().anyMatch(f -> "sinceField".equals(f.getName()))).isTrue();
    }

    @Test
    public void untilAndForVersionFiltersOutFieldsWithOlderVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(4.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().noneMatch(f -> "untilField".equals(f.getName()))).isTrue();
    }

    @Test
    public void untilAndForVersionKeepsFieldsWithNewerVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(3.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().anyMatch(f -> "untilField".equals(f.getName()))).isTrue();
    }

    @Test
    public void sinceAndUntilWithoutVersionAreNotFiltered() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertThat(tsFields.stream().anyMatch(f -> "sinceField".equals(f.getName()))).isTrue();
        assertThat(tsFields.stream().anyMatch(f -> "untilField".equals(f.getName()))).isTrue();
    }

    @Test
    public void sinceIsAddedToComment() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> untilField = tsFields.stream().filter(f -> "untilField".equals(f.getName())).findFirst();

        String comment = untilField.get().getTsComment().getTsCommentSection("version").getCommentText().toString();
        assertThat(comment).isEqualTo("Until version: 4.0");
    }

    @Test
    public void fieldNamingPolicyIsAppliedToFields() throws IOException, NoSuchFieldException {
        gsonObjectMapper.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        String translatedName = LOWER_CASE_WITH_UNDERSCORES.translateName(Product.class.getField("untilField"));
        assertThat(tsFields.stream().anyMatch(f -> translatedName.equals(f.getName()))).isTrue();
    }

    @Test
    public void jsonAdapterIsHandledCorrectlyEvenWithoutTypeMapping() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        assertThat(2).isEqualTo(tsModules.first().getScopedTypesSet().size());
        TSInterface product = findTsInterface(tsModules, "Product");
        Optional<TSField> keyboardField = product.getTsFields().stream().filter(f -> "keyboard".equals(f.getName())).findFirst();
        assertThat("Keyboard").isEqualTo(keyboardField.get().getType().getName());
    }

    @Test
    public void jsonAdapterIsHandledCorrectlyWithTypeMapping() throws IOException {
        TypeMapper.registerTsType(Keyboard.class, TypeMapper.tsNumber);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        assertThat(1).isEqualTo(tsModules.first().getScopedTypesSet().size());
        TSInterface product = (TSInterface) tsModules.first().getScopedTypesSet().first();
        Optional<TSField> keyboardField = product.getTsFields().stream().filter(f -> "keyboard".equals(f.getName())).findFirst();
        assertThat(TypeMapper.tsNumber).isEqualTo(keyboardField.get().getType());
    }

    @Test
    public void nullableStrategyIsAppliedBasedOnNullableAnnotation() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        TSField nullableField = tsFields.stream().filter(f -> "nullableField".equals(f.getName())).findFirst().get();
        assertThat(nullableField.getType() instanceof TSUnion).isTrue();
        TSUnion tsUnion = (TSUnion) nullableField.getType();
        assertThat(tsUnion.getJoinedTsElementList().contains(TypeMapper.tsNull)).isTrue();
    }

    @Test
    public void nullableStrategyIsAppliedBasedOnPrimitiveWrapperTypes() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        TSField intWrapperField = tsFields.stream().filter(f -> "intWrapperField".equals(f.getName())).findFirst().get();
        assertThat(intWrapperField.getType() instanceof TSUnion).isTrue();
        TSUnion tsUnion = (TSUnion) intWrapperField.getType();
        assertThat(tsUnion.getJoinedTsElementList().contains(TypeMapper.tsNull)).isTrue();
    }

    @Test
    public void conversionListenerAllowsToAddTypeFieldToHandleInheritance() throws IOException {
        modelClassesConverter.getConversionListener().getConversionListenerSet().add(new ConversionListener() {
            @Override
            public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
                if (javaType == Keyboard.class) {
                    TSComplexElement keyboardClass = (TSComplexElement) tsScopedElement;
                    keyboardClass.getTsFields().add(new TSField("type", keyboardClass, TypeMapper.tsString));
                }
            }
        });
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSInterface keyboard = findTsInterface(tsModules, "Keyboard");
        SortedSet<TSField> tsFields = keyboard.getTsFields();
        assertThat(tsFields.stream().anyMatch(f -> "type".equals(f.getName()))).isTrue();
    }

    private TSInterface convertProductToTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        return findTsInterface(tsModules, "Product");
    }

    private TSInterface findTsInterface(SortedSet<TSModule> tsModules, String interfaceName) {
        return (TSInterface) tsModules.first().
                getScopedTypesSet()
                .stream()
                .filter(t -> interfaceName.equals(t.getName()))
                .findFirst().get();
    }
}