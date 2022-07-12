package com.blueveery.springrest2ts.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.converters.SpringRestToTsConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tests.ComplexElementFinder;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebFluxControllersMappingTest implements ComplexElementFinder {
    protected Rest2tsGenerator tsGenerator;
    protected Set<String> javaPackageSet;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Product.class));
        JacksonObjectMapper objectMapper = new JacksonObjectMapper();
        tsGenerator.setModelClassesConverter(new ModelClassesToTsInterfacesConverter(objectMapper));
        javaPackageSet = new HashSet();
        javaPackageSet.add(Product.class.getPackage().getName());
        javaPackageSet.add(ProductController.class.getPackage().getName());
        tsGenerator.setRestClassesCondition(new JavaTypeSetFilter(ProductController.class));

        tsGenerator.setRestClassesConverter(new SpringRestToTsConverter(new Angular4ImplementationGenerator()));
        WebFluxConfigurator.configure(tsGenerator);
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

    @Test
    public void monoTypeShouldBeUnpacked() throws IOException {
        TSModule tsModules = tsGenerator
            .convert(javaPackageSet)
            .stream()
            .filter(m -> "springrest2ts-webflux".equals(m.getName()))
            .findFirst()
            .get();
        TSClass controllerClass = (TSClass) findTSComplexElement(
            tsModules,
            ProductController.class.getSimpleName()
        );

        TSMethod getMethod = controllerClass
            .getTsMethods()
            .stream()
            .filter(m -> "get".equals(m.getName()))
            .findFirst()
            .get();
        assertThat(getMethod.getType()).isEqualTo(TypeMapper.map(Product.class));
    }

    @Test
    public void fluxTypeShouldBeConvertedIntoTsArray() throws IOException {
        TSModule tsModules = tsGenerator
            .convert(javaPackageSet)
            .stream()
            .filter(m -> "springrest2ts-webflux".equals(m.getName()))
            .findFirst()
            .get();
        TSClass controllerClass = (TSClass) findTSComplexElement(
            tsModules,
            ProductController.class.getSimpleName()
        );

        TSMethod getAllMethod = controllerClass
            .getTsMethods()
            .stream()
            .filter(m -> "getAll".equals(m.getName()))
            .findFirst()
            .get();
        assertThat(getAllMethod.getType()).isEqualTo(new TSArray(TypeMapper.map(Product.class)));
    }

}