package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.google.gson.ExclusionStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class GsonObjectMapper implements ObjectMapper {

    private boolean excludeFieldsWithoutExposeAnnotation;
    private double forVersion;
    private ExclusionStrategy ExclusionStrategy;

    public GsonObjectMapper() {
    }

    public GsonObjectMapper(
            boolean excludeFieldsWithoutExposeAnnotation, double forVersion,
            ExclusionStrategy exclusionStrategy
    ) {
        this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
        this.forVersion = forVersion;
        ExclusionStrategy = exclusionStrategy;
    }

    public boolean isExcludeFieldsWithoutExposeAnnotation() {
        return excludeFieldsWithoutExposeAnnotation;
    }

    public void setExcludeFieldsWithoutExposeAnnotation(boolean excludeFieldsWithoutExposeAnnotation) {
        this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
    }

    public double getForVersion() {
        return forVersion;
    }

    public void setForVersion(double forVersion) {
        this.forVersion = forVersion;
    }

    public com.google.gson.ExclusionStrategy getExclusionStrategy() {
        return ExclusionStrategy;
    }

    public void setExclusionStrategy(com.google.gson.ExclusionStrategy exclusionStrategy) {
        ExclusionStrategy = exclusionStrategy;
    }

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
        if(excludeFieldsWithoutExposeAnnotation) {
            Expose exposeAnnotation = field.getAnnotation(Expose.class);
            return exposeAnnotation != null && (exposeAnnotation.serialize() || exposeAnnotation.deserialize());
        }
        return true;
    }

    @Override
    public boolean filter(Method method, boolean isGetter) {
        return false;
    }

    @Override
    public String getPropertyName(Field field) {
        SerializedName serializedNameAnnotation = field.getAnnotation(SerializedName.class);
        return serializedNameAnnotation != null? serializedNameAnnotation.value() : field.getName();
    }

    @Override
    public void setIfIsIgnored(Property property, AnnotatedElement annotatedElement) {
    }

    @Override
    public List<TSField> mapJavaPropertyToField(
            Property property, TSComplexElement tsComplexType, ComplexTypeConverter complexTypeConverter,
            ImplementationGenerator implementationGenerator, NullableTypesStrategy nullableTypesStrategy
    ) {
        TSType fieldBaseType = TypeMapper.map(property.getField().getType());
        TSField tsField = new TSField(property.getName(), tsComplexType, fieldBaseType);
        applyExpose(tsField, property);
        return Collections.singletonList(tsField);
    }

    private void applyExpose(TSField tsField, Property property) {
        Expose exposeAnnotation = property.getDeclaredAnnotation(Expose.class);
        if (exposeAnnotation != null) {
            if(!exposeAnnotation.deserialize()) {
                tsField.setReadOnly(true);
            }
            if(!exposeAnnotation.serialize()) {
                TSType fieldType = tsField.getType();
                tsField.setType(new TSUnion(TypeMapper.tsUndefined, fieldType));
            }
        }
    }

    @Override
    public String getPropertyName(Method method, boolean isGetter) {
        throw new UnsupportedOperationException();
    }
}