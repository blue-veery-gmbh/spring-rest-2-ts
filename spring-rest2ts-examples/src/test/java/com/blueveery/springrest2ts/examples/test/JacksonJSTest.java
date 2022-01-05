package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.SpringRestToTsConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.examples.ctrls.spring.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.jacksonjs.JacksonJsConfigurator;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;

import java.io.IOException;
import java.util.function.BiFunction;

public class JacksonJSTest extends TsCodeGenerationsTest {

    @Test
    public void jacksonJSConversionTest() throws IOException {
        TypeMapper.resetTypeMapping();
        tsGenerator = new Rest2tsGenerator();

        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));
        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        jacksonObjectMapper.setAllAccessMethodsVisibility(JsonAutoDetect.Visibility.NONE);
        ImplementationGenerator implementationGenerator = new Angular4ImplementationGenerator();
        restClassesConverter = new SpringRestToTsConverter(implementationGenerator);
        tsGenerator.setRestClassesConverter(restClassesConverter);

        // when there is used JsonTypeInfoId.NAME we need typeIResolver which will generate type name
        BiFunction<Class, Class, String> typeIdResolver = (Class currentType, Class rootType) -> currentType.getSimpleName();

        JacksonJsConfigurator.configure(tsGenerator, jacksonObjectMapper, typeIdResolver);

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
