package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.filters.*;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TsCodeGenerationsTest {

    protected static final Path OUTPUT_DIR_PATH = Paths.get("target/classes/test-webapp/src");

    protected static Rest2tsGenerator tsGenerator;
    protected Set<String> javaPackageSet;
    protected ModelClassesToTsInterfacesConverter modelClassesConverter;
    protected SpringRestToTsConverter restClassesConverter;

    @Before
    public void setUp() throws IOException {
        FileSystemUtils.deleteRecursively(OUTPUT_DIR_PATH.resolve("OUTPUT_DIR_PATH").toFile());

        tsGenerator = new Rest2tsGenerator();

        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));
        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        //set rest class converter
        restClassesConverter = new SpringRestToTsConverter(new Angular4ImplementationGenerator());
        tsGenerator.setRestClassesConverter(restClassesConverter);

        //set java root packages from which class scanning will start
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.examples");
    }

    @Test
    public void customTypeMapping() throws IOException {
        tsGenerator.getCustomTypeMapping().put(UUID.class, TypeMapper.tsString);
        tsGenerator.getCustomTypeMapping().put(BigInteger.class, TypeMapper.tsNumber);
        tsGenerator.getCustomTypeMapping().put(LocalDateTime.class, TypeMapper.tsNumber);
        tsGenerator.getCustomTypeMapping().put(LocalDate.class, new TSArray(TypeMapper.tsNumber));
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
