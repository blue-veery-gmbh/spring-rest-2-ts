package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.JaxRsRestToTsConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.implgens.FetchBasedImplementationGenerator;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;

import java.io.IOException;


public class JaxRsGenerationTest extends TsCodeGenerationsTest {

    @Test
    public void jaxRsToAngular2PlusConverterTest() throws IOException {
        Angular4ImplementationGenerator implementationGenerator = new Angular4ImplementationGenerator();
        jaxRsConverterTest(implementationGenerator);
    }

    @Test
    public void jaxRsToPromiseAPIConverterTest() throws IOException {
        FetchBasedImplementationGenerator implementationGenerator = new FetchBasedImplementationGenerator();
        jaxRsConverterTest(implementationGenerator);
    }

    private void jaxRsConverterTest(ImplementationGenerator implementationGenerator) throws IOException {
        tsGenerator = new Rest2tsGenerator();

        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));
        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        ModelClassesToTsInterfacesConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        SubstringClassNameMapper classNameMapper = new SubstringClassNameMapper("ResourceImpl", "Service");
        JaxRsRestToTsConverter jaxRsRestToTsConverter = new JaxRsRestToTsConverter(implementationGenerator, classNameMapper);
        tsGenerator.setRestClassesConverter(jaxRsRestToTsConverter);


        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
