package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.google.gson.ExclusionStrategy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class GsonObjectMapper implements ObjectMapper {

    private boolean excludeFieldsWithoutExposeAnnotation;
    private double forVersion;
    private ExclusionStrategy ExclusionStrategy;

    @Override
    public List<TSField> addTypeLevelSpecificFields(
            Class javaType, TSComplexElement tsComplexType
    ) {
        return Collections.emptyList();
    }

    @Override
    public boolean filterClass(Class clazz) {
        return true;
    }

    @Override
    public boolean filter(Field field) {
        return true;
    }

    @Override
    public boolean filter(Method method, boolean isGetter) {
        return false;
    }

    @Override
    public String getPropertyName(Field field) {
        return field.getName();
    }

    @Override
    public void setIfIsIgnored(Property property, AnnotatedElement annotatedElement) {
    }

    @Override
    public List<TSField> mapJavaPropertyToField(
            Property property, TSComplexElement tsComplexType, ComplexTypeConverter complexTypeConverter,
            ImplementationGenerator implementationGenerator, NullableTypesStrategy nullableTypesStrategy
    ) {
        TSField tsField = new TSField(property.getName(), tsComplexType, TypeMapper.map(property.getField().getType()));
        return Collections.singletonList(tsField);
    }

    @Override
    public String getPropertyName(Method method, boolean isGetter) {
        throw new UnsupportedOperationException();
    }
}