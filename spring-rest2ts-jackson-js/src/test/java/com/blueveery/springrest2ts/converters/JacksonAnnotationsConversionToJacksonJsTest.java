package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSImport;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonAnnotationsConversionToJacksonJsTest extends JacksonJsTest {
    private JacksonAnnotationsConversionToJacksonJs jacksonAnnotationsConversion;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Vehicle.class));
        jacksonAnnotationsConversion = new JacksonAnnotationsConversionToJacksonJs();
        modelClassesConverter.getConversionListener().getConversionListenerSet().add(jacksonAnnotationsConversion);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.converters");
    }

    @Test
    public void jsonTypeInfoDecoratorShouldBeImportedWhenAdded() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSImport tsImport = tsModules.first().getImportMap().get(jacksonAnnotationsConversion.jacksonJSModule);
        assertThat(tsImport).isNotNull();
        assertThat(tsImport.getWhat()).contains(jacksonAnnotationsConversion.jsonTypeInfoFunction);
        assertThat(tsImport.getWhat()).contains(jacksonAnnotationsConversion.jsonTypeInfoIdEnum);
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldBeAddedBasedOnJsonTypeInfo() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass vehicle = (TSClass) findTSComplexElement(tsModules, Vehicle.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, vehicle.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("use")).isEqualTo(new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoId.NAME"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldHaveIncludeSetToProperty() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("include")).isEqualTo(new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoAs.PROPERTY"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldHaveIncludeSetToWRAPPER_OBJECT() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("include")).isEqualTo(new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoAs.WRAPPER_OBJECT"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldHaveIncludeSetToWRAPPER_ARRAY() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("include")).isEqualTo(new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoAs.WRAPPER_ARRAY"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldSkipPropertyIfDefaultIsUsed() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("property")).isNull();
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldSetCorrectPropertyIfClassIsUsed() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("property")).isEqualTo(new TSLiteral("", TypeMapper.tsString, "@class"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldSetCorrectPropertyIfMinimalClassIsUsed() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("property")).isEqualTo(new TSLiteral("", TypeMapper.tsString, "@c"));
        printTSElement(tsModules.first());
    }

    @Test
    public void jsonTypeInfoDecoratorShouldSetCorrectPropertyIfCustomNameIsUsed() throws IOException {
        @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "typeId")
        class Animal {}
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Animal.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass animal = (TSClass) findTSComplexElement(tsModules, Animal.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, animal.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral jsonLiteral = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().stream().findFirst().get();
        assertThat(jsonLiteral.getFieldMap().get("property")).isEqualTo(new TSLiteral("", TypeMapper.tsString, "typeId"));
        printTSElement(tsModules.first());
    }

    @Test
    public void addJsonSubTypesShouldBeConvertedCorrectlyIfIdNameIsUsed() throws IOException {
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Vehicle.class, Truck.class, Car.class));
        SortedSet<TSModule> tsModules = tsGenerator.convert(Sets.set(getClass().getPackage().getName()));
        TSClass vehicle = (TSClass) findTSComplexElement(tsModules, Vehicle.class.getSimpleName());
        Optional<TSDecorator> jsonSubTypes = findDecorator(jacksonAnnotationsConversion.jsonSubTypesFunction, vehicle.getTsDecoratorList());
        assertThat(jsonSubTypes).isPresent();
        TSLiteralArray tsLiteralArray = (TSLiteralArray) jsonSubTypes.get().getTsLiteralList().stream().findFirst().get();
        assertThat(tsLiteralArray.getLiteralList()).hasSize(2);
        printTSElement(tsModules.first());
    }
}