package com.blueveery.springrest2ts.tests;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.ModelClassesAbstractConverter;
import com.blueveery.springrest2ts.converters.ModelClassesToTsClassesConverter;
import com.blueveery.springrest2ts.converters.ObjectMapper;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import java.util.Collections;
import java.util.Set;
import org.junit.After;
import org.junit.Before;

public abstract class BaseTest<M extends ObjectMapper> implements ComplexElementFinder {
    protected Rest2tsGenerator tsGenerator;
    protected M objectMapper;
    protected Set<String> javaPackageSet;
    protected ModelClassesAbstractConverter modelClassesConverter;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class, KeyboardInterface.class));
        objectMapper = createObjectMapper();
        modelClassesConverter = getModelClassesConverter();
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.tests.model");
    }

    protected abstract M createObjectMapper();

    protected ModelClassesAbstractConverter getModelClassesConverter() {
        return new ModelClassesToTsClassesConverter(new EmptyImplementationGenerator(), objectMapper);
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

}
