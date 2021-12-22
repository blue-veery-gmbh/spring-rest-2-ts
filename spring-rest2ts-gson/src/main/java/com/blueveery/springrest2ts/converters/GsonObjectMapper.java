package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.internal.Excluder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class GsonObjectMapper implements ObjectMapper {

    private Excluder excluder = Excluder.DEFAULT;
    private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;

    public GsonObjectMapper() {
    }

    public Excluder getExcluder() {
        return excluder;
    }

    public void setExcluder(Excluder excluder) {
        this.excluder = excluder;
    }

    public FieldNamingStrategy getFieldNamingPolicy() {
        return fieldNamingPolicy;
    }

    public void setFieldNamingPolicy(FieldNamingStrategy fieldNamingPolicy) {
        this.fieldNamingPolicy = fieldNamingPolicy;
    }

    @Override
    public List<TSField> addTypeLevelSpecificFields(
            Class javaType, TSComplexElement tsComplexType
    ) {
        return Collections.emptyList();
    }

    @Override
    public boolean filterClass(Class clazz) {
        return !(excluder.excludeClass(clazz, true) && excluder.excludeClass(clazz, true));
    }

    @Override
    public boolean filter(Field field) {
        return !(excluder.excludeField(field, true) && excluder.excludeField(field, false));
    }

    @Override
    public boolean filter(Method method, boolean isGetter) {
        return false;
    }

    @Override
    public String getPropertyName(Field field) {
        SerializedName serializedNameAnnotation = field.getAnnotation(SerializedName.class);
        if (serializedNameAnnotation != null) {
            return serializedNameAnnotation.value();
        }
        return fieldNamingPolicy.translateName(field);
    }

    @Override
    public void setIfIsIgnored(Property property, AnnotatedElement annotatedElement) {
    }

    @Override
    public List<TSField> mapJavaPropertyToField(
            Property property, TSComplexElement tsComplexType, ComplexTypeConverter complexTypeConverter,
            ImplementationGenerator implementationGenerator, NullableTypesStrategy nullableTypesStrategy
    ) {
        TSType fieldBaseType = TypeMapper.map(property.getField().getGenericType());
        TSField tsField = new TSField(property.getName(), tsComplexType, fieldBaseType, property);
        tsField.addAllAnnotations(property.getDeclaredAnnotations());

        applyExpose(tsField, property);
        applySince(tsField, property);
        applyUntil(tsField, property);
        return Collections.singletonList(tsField);
    }

    private void applySince(TSField tsField, Property property) {
        Since sinceAnnotation = property.getDeclaredAnnotation(Since.class);
        if (sinceAnnotation != null) {
            StringBuilder commentText = tsField.getTsComment().getTsCommentSection("version").getCommentText();
            commentText.append("Since version: ").append(sinceAnnotation.value());
        }
    }

    private void applyUntil(TSField tsField, Property property) {
        Until untilAnnotation = property.getDeclaredAnnotation(Until.class);
        if (untilAnnotation != null) {
            StringBuilder commentText = tsField.getTsComment().getTsCommentSection("version").getCommentText();
            if (commentText.length() > 0) {
                commentText.append("\t");
            }
            commentText.append("Until version: ").append(untilAnnotation.value());
        }
    }

    private void applyExpose(TSField tsField, Property property) {
        Expose exposeAnnotation = property.getDeclaredAnnotation(Expose.class);
        if (exposeAnnotation != null) {
            if (!exposeAnnotation.deserialize()) {
                tsField.setReadOnly(true);
            }
            if (!exposeAnnotation.serialize()) {
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