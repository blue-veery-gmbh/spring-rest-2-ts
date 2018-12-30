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

        return true;
    }

    private boolean containsIgnoreTypeAnnotation(Class<?> type) {
        JsonIgnoreType jsonIgnoreType = type.getDeclaredAnnotation(JsonIgnoreType.class);
        return jsonIgnoreType != null && jsonIgnoreType.value();
    }


    @Override
    public List<TSField> mapToField(Field field, TSComplexType tsComplexType) {
        List<TSField> tsFieldList = new ArrayList<>();
        Type fieldJavaType = field.getGenericType();
        fieldJavaType = applyJsonValue(fieldJavaType);
        TSType fieldType = TypeMapper.map(fieldJavaType);
        TSField tsField = new TSField(field.getName(), tsComplexType, fieldType);
        applyJsonProperty(tsField, field.getDeclaredAnnotation(JsonProperty.class));
        if(applyJsonIgnoreProperties(tsField, field.getDeclaringClass())) {
            applyJsonFormat(tsField, field.getDeclaredAnnotation(JsonFormat.class));
            tsFieldList.add(tsField);
        }
        return tsFieldList;
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
                        return true;
                    }
                    if (jsonIgnoreProperties.allowSetters()) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return true;
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
    public List<TSField> mapToField(Method method, TSComplexType tsComplexType) {
        List<TSField> tsFieldList = new ArrayList<>();
        return tsFieldList;
    }

    @Override
    public void addTypeLevelSpecificFields(Class javaType, TSComplexType tsComplexType) {
        JsonTypeInfo jsonTypeInfoAnnotation = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
        if(jsonTypeInfoAnnotation!=null){
            switch(jsonTypeInfoAnnotation.include()){

                case PROPERTY:
                case WRAPPER_OBJECT:
                case WRAPPER_ARRAY:
                    TSField tsField = new TSField(jsonTypeInfoAnnotation.property(), tsComplexType, TypeMapper.map(String.class));
                    tsComplexType.addTsField(tsField);
                    break;
                case EXTERNAL_PROPERTY:
                case EXISTING_PROPERTY:
                    break;
            }
        }
    }
}
