package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.JaxRsRestToTsConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.implgens.FetchBasedImplementationGenerator;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.mincong.shop.rest.ProductResourceImpl;
import io.mincong.shop.rest.dto.Product;
import io.mincong.shop.rest.dto.ProductCreated;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


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

        //set java model type filters
        Set<Class> dtoTypes = new HashSet<>();
        dtoTypes.add(Product.class);
        dtoTypes.add(ProductCreated.class);
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(dtoTypes));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        ModelClassesToTsInterfacesConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        //set java rest controllers type filters
        Set<Class> resourcesTypes = new HashSet<>();
        resourcesTypes.add(ProductResourceImpl.class);
        tsGenerator.setRestClassesCondition(new JavaTypeSetFilter(resourcesTypes));

        SubstringClassNameMapper classNameMapper = new SubstringClassNameMapper("ResourceImpl", "Service");
        JaxRsRestToTsConverter jaxRsRestToTsConverter = new JaxRsRestToTsConverter(implementationGenerator, classNameMapper);
        tsGenerator.setRestClassesConverter(jaxRsRestToTsConverter);


        Set<String> javaPackageSet = new HashSet<>();
        javaPackageSet.add(Product.class.getPackage().getName());
        javaPackageSet.add(ProductResourceImpl.class.getPackage().getName());
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
