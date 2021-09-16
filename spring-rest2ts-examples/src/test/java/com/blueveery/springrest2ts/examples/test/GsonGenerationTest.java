package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.GsonObjectMapper;
import com.blueveery.springrest2ts.converters.JaxRsRestToTsConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import com.google.gson.internal.Excluder;
import org.junit.Test;

import java.io.IOException;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class GsonGenerationTest extends TsCodeGenerationsTest {

    @Test
    public void gsonObjectMapperWithAngular() throws IOException {
        tsGenerator = new Rest2tsGenerator();

        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));
        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        //set model class converter
        GsonObjectMapper gsonObjectMapper = new GsonObjectMapper();
        gsonObjectMapper.setExcluder(Excluder.DEFAULT.withVersion(3.0)); //configure  Excluder as it is done in serialization setup
        gsonObjectMapper.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES); //you can use required naming policy

        ModelClassesToTsInterfacesConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(gsonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        ClassNameMapper classNameMapper = new SubstringClassNameMapper("ResourceImpl", "Service");
        JaxRsRestToTsConverter jaxRsRestToTsConverter = new JaxRsRestToTsConverter(new Angular4ImplementationGenerator(), classNameMapper);
        tsGenerator.setRestClassesConverter(jaxRsRestToTsConverter);

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
