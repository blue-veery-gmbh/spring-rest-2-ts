package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tests.BaseTest;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeBasedJacksonJsConversionTest extends BaseTest<JacksonObjectMapper> {

    private TypeBasedJacksonJsConversion typeBasedJacksonJsConversion;

    @Override
    protected JacksonObjectMapper createObjectMapper() {
        return new JacksonObjectMapper();
    }

    @Override
    protected ModelClassesAbstractConverter getModelClassesConverter() {
        ModelClassesToTsClassesConverter modelClassesToTsClassesConverter = new ModelClassesToTsClassesConverter(new EmptyImplementationGenerator(), objectMapper);
        typeBasedJacksonJsConversion = new TypeBasedJacksonJsConversion();
        modelClassesToTsClassesConverter.getConversionListener().getConversionListenerSet().add(typeBasedJacksonJsConversion);
        return modelClassesToTsClassesConverter;
    }

    @Test
    public void classFieldShouldHaveJsonPropertyDecorator() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard.getTsFields().first().getTsDecoratorList()).contains(typeBasedJacksonJsConversion.jsonProperty);
        printClass(keyboard);

    }

    private void printClass(TSClass tsClass) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        tsClass.write(writer);
        writer.flush();
    }
}