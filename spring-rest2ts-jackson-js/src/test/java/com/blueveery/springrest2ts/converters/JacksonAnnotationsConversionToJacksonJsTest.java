package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSImport;
import com.blueveery.springrest2ts.tsmodel.TSModule;
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
    public void jsonTypeInfoDecoratorShouldBeAddedBasedOnJsonTypeInfo() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass vehicle = (TSClass) findTSComplexElement(tsModules, Vehicle.class.getSimpleName());
        Optional<TSDecorator> jsonClassType = findDecorator(jacksonAnnotationsConversion.jsonTypeInfoFunction, vehicle.getTsDecoratorList());
        assertThat(jsonClassType).isPresent();
        printTSElement(tsModules.first());
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
}