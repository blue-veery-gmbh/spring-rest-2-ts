package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertTrue;

public class GsonObjectMapperTest {

    private Rest2tsGenerator tsGenerator;
    private GsonObjectMapper gsonObjectMapper;
    private Set<String> javaPackageSet;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Collections.singleton(Product.class)));
        gsonObjectMapper = new GsonObjectMapper();
        ModelClassesAbstractConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(gsonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.converters");
    }

    @Test
    public void testGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        System.out.println(gson.toJson(new Product()));
    }

    @Test
    public void transientFieldsAreSkippedInTsCode() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSInterface productTsInterface = (TSInterface) tsModules.first().getScopedTypesSet().first();
        assertTrue(productTsInterface.getTsFields().stream().allMatch(f -> !"tempName".equals(f.getName())));
    }

    @Test
    public void nonTransientFieldsAreIncludedInTsCode() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSInterface productTsInterface = (TSInterface) tsModules.first().getScopedTypesSet().first();
        assertTrue(productTsInterface.getTsFields().stream().anyMatch(f -> "name".equals(f.getName())));
    }
}