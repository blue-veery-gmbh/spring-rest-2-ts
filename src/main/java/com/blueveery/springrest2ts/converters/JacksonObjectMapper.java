package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.fasterxml.jackson.annotation.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonObjectMapper implements ObjectMapper {
    JsonAutoDetect.Visibility fieldsVisibility = JsonAutoDetect.Visibility.NONE;
    JsonAutoDetect.Visibility gettersVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY;
    JsonAutoDetect.Visibility isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY;
    private final Map<Class, List<JsonIgnoreProperties>> jsonIgnorePropertiesPerClass = new HashMap<>();

    public JacksonObjectMapper() {
    }

    public JacksonObjectMapper(JsonAutoDetect.Visibility fieldsVisibility, JsonAutoDetect.Visibility gettersVisibility, JsonAutoDetect.Visibility isGetterVisibility) {
        this.fieldsVisibility = fieldsVisibility;
        this.gettersVisibility = gettersVisibility;
        this.isGetterVisibility = isGetterVisibility;
    }

    @Override
    public boolean filter(Member member, TSComplexType tsComplexType) {
        if(member instanceof Field){
            Field field = (Field) member;
            if(!fieldsVisibility.isVisible(field)){
                return false;
            }
            if(containsIgnoreTypeAnnotation(field.getType())){
                return false;
            }

        }
        if (member instanceof Method){
            Method method = (Method) member;
            if(!(member.getName().startsWith("get") && gettersVisibility.isVisible(method))){
                return false;
            }
            if(!(member.getName().startsWith("is") && isGetterVisibility.isVisible(method))){
                return false;
            }
            if(containsIgnoreTypeAnnotation(method.getReturnType())){
                return false;
            }
        }

        JsonIgnore jsonIgnore = ((AccessibleObject) member).getDeclaredAnnotation(JsonIgnore.class);
        if(jsonIgnore != null && jsonIgnore.value()){
            return false;
        }

        JsonBackReference jsonBackReference = ((AccessibleObject) member).getDeclaredAnnotation(JsonBackReference.class);
        if (jsonBackReference != null) {
            return false;
        }

        return true;
    }


    @Override
    public List<TSField> mapToField(Field field, TSComplexType tsComplexType, ComplexTypeConverter complexTypeConverter) {
        List<TSField> tsFieldList = new ArrayList<>();
        Type fieldJavaType = field.getGenericType();
        fieldJavaType = applyJsonValue(fieldJavaType);
        if(!applyJsonUnwrapped((Class) fieldJavaType, field.getDeclaredAnnotation(JsonUnwrapped.class), tsComplexType, tsFieldList, complexTypeConverter)) {
            TSType fieldType = TypeMapper.map(fieldJavaType);
            TSField tsField = new TSField(field.getName(), tsComplexType, fieldType);
            if (!applyJsonIgnoreProperties(tsField, field.getDeclaringClass())) {
                applyJsonProperty(tsField, field.getDeclaredAnnotation(JsonProperty.class));
                applyJsonFormat(tsField, field.getDeclaredAnnotation(JsonFormat.class));
                applyJacksonInject(tsField, field.getDeclaredAnnotation(JacksonInject.class));
                applyJsonRawValue(tsField, field.getDeclaredAnnotation(JsonRawValue.class));
                tsFieldList.add(tsField);
            }
        }
        return tsFieldList;
    }

    private void applyJsonRawValue(TSField tsField, JsonRawValue jsonRawValue){
        if (jsonRawValue != null && jsonRawValue.value()) {
            tsField.setType(TypeMapper.tsAny);
        }
    }

    private void applyJacksonInject(TSField tsField, JacksonInject jacksonInject) {
        if (jacksonInject != null) {
            tsField.setReadOnly(true);
        }
    }

    private boolean containsIgnoreTypeAnnotation(Class<?> type) {
        JsonIgnoreType jsonIgnoreType = type.getDeclaredAnnotation(JsonIgnoreType.class);
        return jsonIgnoreType != null && jsonIgnoreType.value();
    }

    private boolean applyJsonUnwrapped(Class fieldJavaType, JsonUnwrapped declaredAnnotation, TSComplexType tsComplexType, List<TSField> tsFieldList, ComplexTypeConverter complexTypeConverter) {
        if (declaredAnnotation != null) {
            TSType tsType = TypeMapper.map(fieldJavaType);
            if(!(tsType instanceof TSComplexType)){
                return false;
            }
            TSComplexType referredTsType = (TSComplexType) tsType;
            if(!referredTsType.isConverted()){
                complexTypeConverter.convert(fieldJavaType);
            }
            for (TSField nextTsField : referredTsType.getTsFields()) {
                tsFieldList.add(new TSField(nextTsField.getName(), tsComplexType, nextTsField.getType()));
            }
            return true;
        }
        return false;
    }

    private void applyJsonFormat(TSField tsField, JsonFormat jsonFormat) {
        if (jsonFormat != null) {
            switch (jsonFormat.shape()) {
                case ANY: tsField.setType(TypeMapper.tsAny);
                    return;
                case SCALAR: tsField.setType(TypeMapper.tsAny);
                    return;
                case ARRAY: tsField.setType(new TSArray(TypeMapper.tsAny));
                    return;
                case OBJECT: tsField.setType(TypeMapper.tsAny);
                    return;
                case NUMBER: tsField.setType(TypeMapper.tsNumber);
                    return;
                case NUMBER_FLOAT: tsField.setType(TypeMapper.tsNumber);
                    return;
                case NUMBER_INT: tsField.setType(TypeMapper.tsNumber);
                    return;
                case STRING: tsField.setType(TypeMapper.tsString);
                    return;
                case BOOLEAN: tsField.setType(TypeMapper.tsBoolean);
                    return;
            }
        }
    }

    private boolean applyJsonIgnoreProperties(TSField tsField, Class<?> declaringClass) {
        List<JsonIgnoreProperties> jsonIgnorePropertiesList = discoverJsonIgnoreProperties(declaringClass);
        for (JsonIgnoreProperties jsonIgnoreProperties : jsonIgnorePropertiesList) {
            for (String propertyName : jsonIgnoreProperties.value()) {
                if(propertyName.trim().equals(tsField.getName())){
                    if (jsonIgnoreProperties.allowGetters()) {
                        tsField.setReadOnly(true);
                        return false;
                    }
                    if (jsonIgnoreProperties.allowSetters()) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private List<JsonIgnoreProperties> discoverJsonIgnoreProperties(Class<?> declaringClass) {
        if(!jsonIgnorePropertiesPerClass.containsKey(declaringClass)) {
            List<JsonIgnoreProperties> jsonIgnorePropertiesList = new ArrayList<>();
            JsonIgnoreProperties jsonIgnoreProperties = declaringClass.getAnnotation(JsonIgnoreProperties.class);
            if (jsonIgnoreProperties != null) {
                jsonIgnorePropertiesList.add(jsonIgnoreProperties);
            }
            for (Field field : declaringClass.getDeclaredFields()) {
                jsonIgnoreProperties = field.getAnnotation(JsonIgnoreProperties.class);
                if (jsonIgnoreProperties != null) {
                    jsonIgnorePropertiesList.add(jsonIgnoreProperties);
                }
            }
            for (Constructor<?> constructor : declaringClass.getDeclaredConstructors()) {
                jsonIgnoreProperties = constructor.getAnnotation(JsonIgnoreProperties.class);
                if (jsonIgnoreProperties != null) {
                    jsonIgnorePropertiesList.add(jsonIgnoreProperties);
                }
            }
            for (Method method : declaringClass.getDeclaredMethods()) {
                jsonIgnoreProperties = method.getAnnotation(JsonIgnoreProperties.class);
                if (jsonIgnoreProperties != null) {
                    jsonIgnorePropertiesList.add(jsonIgnoreProperties);
                }
            }

            jsonIgnorePropertiesPerClass.put(declaringClass, jsonIgnorePropertiesList);
        }
        return jsonIgnorePropertiesPerClass.get(declaringClass);
    }

    private Type applyJsonValue(Type fieldJavaType) {
        if(fieldJavaType instanceof Class){
            Class fieldClass = (Class) fieldJavaType;
            for (Method method : fieldClass.getMethods()) {
                JsonValue jsonValue = method.getDeclaredAnnotation(JsonValue.class);
                if(jsonValue != null && jsonValue.value()){
                    return method.getReturnType();
                }
            }
        }
        return fieldJavaType;
    }

    private void applyJsonProperty(TSField tsField, JsonProperty jsonProperty) {
        if(jsonProperty != null){
            if(!JsonProperty.USE_DEFAULT_NAME.equals(jsonProperty.value())){
                tsField.setName(jsonProperty.value());
            }
            if(jsonProperty.access() == JsonProperty.Access.READ_ONLY){
                tsField.setReadOnly(true);
            }
            tsField.setOptional(!jsonProperty.required());
        }
    }

    @Override
    public List<TSField> mapToField(Method method, TSComplexType tsComplexType, ComplexTypeConverter complexTypeConverter) {
        List<TSField> tsFieldList = new ArrayList<>();
        return tsFieldList;
    }

    @Override
    public void addTypeLevelSpecificFields(Class javaType, TSComplexType tsComplexType) {
        JsonTypeInfo jsonTypeInfoAnnotation = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
        if(jsonTypeInfoAnnotation!=null){
            switch(jsonTypeInfoAnnotation.include()){
                case PROPERTY:
                    String propertyName = jsonTypeInfoAnnotation.property();
                    if("".equals(propertyName)){
                        propertyName = jsonTypeInfoAnnotation.use().toString();
                    }
                    for (TSField tsField : tsComplexType.getTsFields()) {
                        if (propertyName.equals(tsField.getName())) {
                            return;
                        }
                    }
                    TSField tsField = new TSField(propertyName, tsComplexType, TypeMapper.tsString);
                    tsComplexType.addTsField(tsField);
                    break;
                case WRAPPER_OBJECT:
                case WRAPPER_ARRAY:
                case EXTERNAL_PROPERTY:
                case EXISTING_PROPERTY:
                    break;
            }
        }
    }
}
