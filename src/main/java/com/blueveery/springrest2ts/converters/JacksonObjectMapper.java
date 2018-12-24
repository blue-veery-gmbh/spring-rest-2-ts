package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.fasterxml.jackson.annotation.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JacksonObjectMapper implements ObjectMapper {
    JsonAutoDetect.Visibility fieldsVisibility = JsonAutoDetect.Visibility.NONE;
    JsonAutoDetect.Visibility gettersVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY;
    JsonAutoDetect.Visibility isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY;

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
        TSType fieldType = TypeMapper.map(field.getGenericType());
        TSField tsField = new TSField(field.getName(), tsComplexType, fieldType);
        applyJsonProperty(tsField, field.getDeclaredAnnotation(JsonProperty.class));
        tsFieldList.add(tsField);
        return tsFieldList;
    }

    private void applyJsonProperty(TSField tsField, JsonProperty jsonProperty) {
        if(jsonProperty != null){
            if(!JsonProperty.USE_DEFAULT_NAME.equals(jsonProperty.value())){
                tsField.setName(jsonProperty.value());
            }
            if(jsonProperty.access() == JsonProperty.Access.READ_ONLY){
                tsField.setReadOnly(true);
            }
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
