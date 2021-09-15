package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GsonObjectMapperTest {

    private Rest2tsGenerator tsGenerator;
    private GsonObjectMapper gsonObjectMapper;
    private Set<String> javaPackageSet;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(new HashSet<>(Arrays.asList(Product.class, Keyboard.class))));
        gsonObjectMapper = new GsonObjectMapper();
        ModelClassesAbstractConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(gsonObjectMapper);
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
        assertTrue(productTsInterface.getTsFields().stream().allMatch(f -> !"tempName".equals(f.getName())));
    }

    @Test
    public void nonTransientFieldsAreIncludedInTsCode() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertTrue(productTsInterface.getTsFields().stream().anyMatch(f -> "name".equals(f.getName())));
    }

    @Test
    public void serializedNameChangesFieldName() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertTrue(productTsInterface.getTsFields().stream().anyMatch(f -> "year".equals(f.getName())));
        assertTrue(productTsInterface.getTsFields().stream().noneMatch(f -> "productionYear".equals(f.getName())));
    }

    @Test
    public void exposeFiltersFields() throws IOException {
        List<String> exposedFields = Arrays.asList("exposedName", "serializedOnly", "deserializedOnly");
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().allMatch(f -> exposedFields.contains(f.getName())));
        Optional<TSField> exposedNameField = tsFields.stream().filter(f -> "exposedName".equals(f.getName())).findFirst();
        assertEquals(exposedNameField.get().getType(), TypeMapper.tsString);
    }

    @Test
    public void serializedOnlyFieldIsReadonly() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> serializedOnlyField = tsFields.stream().filter(f -> "serializedOnly".equals(f.getName())).findFirst();
        assertEquals(serializedOnlyField.get().getType(), TypeMapper.tsString);
        assertTrue(serializedOnlyField.get().getReadOnly());
    }

    @Test
    public void deserializedOnlyFieldIsOptional() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> deserializedOnlyField = tsFields.stream().filter(f -> "deserializedOnly".equals(f.getName())).findFirst();
        assertEquals(deserializedOnlyField.get().getType(), new TSUnion(TypeMapper.tsUndefined, TypeMapper.tsString));
    }

    @Test
    public void sinceAndForVersionFiltersOutFieldsWithNewerVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(1.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().noneMatch(f -> "sinceField".equals(f.getName())));
    }

    @Test
    public void sinceAndForVersionKeepsFieldsWithOlderVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(2.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().anyMatch(f -> "sinceField".equals(f.getName())));
    }

    @Test
    public void untilAndForVersionFiltersOutFieldsWithOlderVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(4.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().noneMatch(f -> "untilField".equals(f.getName())));
    }

    @Test
    public void untilAndForVersionKeepsFieldsWithNewerVersion() throws IOException {
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(3.0));
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().anyMatch(f -> "untilField".equals(f.getName())));
    }

    @Test
    public void sinceAndUntilWithoutVersionAreNotFiltered() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().anyMatch(f -> "sinceField".equals(f.getName())));
        assertTrue(tsFields.stream().anyMatch(f -> "untilField".equals(f.getName())));
    }

    @Test
    public void sinceIsAddedToComment() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> untilField = tsFields.stream().filter(f -> "untilField".equals(f.getName())).findFirst();

        String comment = untilField.get().getTsComment().getTsCommentSection("version").getCommentText().toString();
        assertEquals("Until version: 4.0", comment);
    }

    @Test
    public void fieldNamingPolicyIsAppliedToFields() throws IOException, NoSuchFieldException {
        gsonObjectMapper.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        String translatedName = LOWER_CASE_WITH_UNDERSCORES.translateName(Product.class.getField("untilField"));
        assertTrue(tsFields.stream().anyMatch(f -> translatedName.equals(f.getName())));
    }

    @Test
    public void jsonAdapterIsHandledCorrectlyEvenWithoutTypeMapping() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        assertEquals(tsModules.first().getScopedTypesSet().size(), 1);
        TSInterface product = (TSInterface) tsModules.first().getScopedTypesSet().first();
        Optional<TSField> keyboardField = product.getTsFields().stream().filter(f -> "keyboard".equals(f.getName())).findFirst();
        assertEquals(keyboardField.get().getType(), TypeMapper.tsAny);
    }

    @Test
    public void jsonAdapterIsHandledCorrectlyWithTypeMapping() throws IOException {
        TypeMapper.registerTsType(Keyboard.class, TypeMapper.tsNumber);
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        assertEquals(tsModules.first().getScopedTypesSet().size(), 1);
        TSInterface product = (TSInterface) tsModules.first().getScopedTypesSet().first();
        Optional<TSField> keyboardField = product.getTsFields().stream().filter(f -> "keyboard".equals(f.getName())).findFirst();
        assertEquals(keyboardField.get().getType(), TypeMapper.tsNumber);
    }

    @Test
    public void nullableStrategyIsAppliedBasedOnNullableAnnotation() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        TSField nullableField = tsFields.stream().filter(f -> "nullableField".equals(f.getName())).findFirst().get();
        assertTrue(nullableField.getType() instanceof TSUnion);
        TSUnion tsUnion = (TSUnion) nullableField.getType();
        assertTrue(tsUnion.getJoinedTsElementList().contains(TypeMapper.tsNull));
    }

    @Test
    public void nullableStrategyIsAppliedBasedOnPrimitiveWrapperTypes() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        TSField intWrapperField = tsFields.stream().filter(f -> "intWrapperField".equals(f.getName())).findFirst().get();
        assertTrue(intWrapperField.getType() instanceof TSUnion);
        TSUnion tsUnion = (TSUnion) intWrapperField.getType();
        assertTrue(tsUnion.getJoinedTsElementList().contains(TypeMapper.tsNull));
    }

    private TSInterface convertProductToTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        return (TSInterface) tsModules.first().getScopedTypesSet().first();
    }
}