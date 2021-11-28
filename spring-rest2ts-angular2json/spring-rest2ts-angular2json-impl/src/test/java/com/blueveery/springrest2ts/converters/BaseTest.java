package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.junit.After;
import org.junit.Before;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

public class BaseTest {
    protected Rest2tsGenerator tsGenerator;
    protected JacksonObjectMapper objectMapper;
    protected Set<String> javaPackageSet;
    protected ModelClassesAbstractConverter modelClassesConverter;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(createClassFilter());
        objectMapper = new JacksonObjectMapper();
        modelClassesConverter = new ModelClassesToTsAngular2JsonApiConverter(objectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.tests.model");
    }

    protected JavaTypeSetFilter createClassFilter() {
        return new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class, KeyboardInterface.class);
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

    protected TSComplexElement findTSComplexElement(SortedSet<TSModule> tsModules, String name) {
        return (TSComplexElement) tsModules.first().
                getScopedTypesSet()
                .stream()
                .filter(t -> name.equals(t.getName()))
                .findFirst().get();
    }
}
