package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.JavaPackageToTsModuleConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsAngular2JsonApiConverter;
import com.blueveery.springrest2ts.examples.model.ManufacturerDTO;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;

import java.io.IOException;


public class Angular2JsonApiTest extends TsCodeGenerationsTest {

    @Test
    public void angular2JsonApiConverterTest() throws IOException {
        tsGenerator = new Rest2tsGenerator();

        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        ModelClassesToTsAngular2JsonApiConverter modelClassesConverter = new ModelClassesToTsAngular2JsonApiConverter(jacksonObjectMapper);

        //models variable is optional, if not set it will not be generated, module selection is up to user decision
        JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = tsGenerator.getJavaPackageToTsModuleConverter();
        TSModule tsModuleForModelsVariable = javaPackageToTsModuleConverter.getTsModule(ManufacturerDTO.class);
        modelClassesConverter.createModelsVariable("models", tsModuleForModelsVariable);
        tsGenerator.setModelClassesConverter(modelClassesConverter);


        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
