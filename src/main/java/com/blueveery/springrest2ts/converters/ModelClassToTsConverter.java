package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassToTsConverter extends ComplexTypeConverter {
    public void preConvert(ModuleConverter moduleConverter, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny){
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            TSInterface tsInterface = new TSInterface(javaClass.getSimpleName(), tsModule);
            tsModule.addScopedType(tsInterface);
            TypeMapper.registerTsType(javaClass, tsInterface);
        }

    }
    @Override
    public void convert(ModuleConverter moduleConverter, GenerationContext generationContext, Class javaType) {
        TSInterface tsInterface = (TSInterface) TypeMapper.map(javaType);
        if(javaType.getSuperclass() != Object.class) {
            TSType superClass = TypeMapper.map(javaType.getSuperclass());
            if (superClass != TypeMapper.tsAny) {
                tsInterface.addExtendsInterfaces((TSInterface) superClass);
            }
        }

        convertJsonTypeInfoAnnotation(javaType, tsInterface);

        for(Field field:javaType.getDeclaredFields()){
            if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())){
                TSType fieldType = TypeMapper.map(field.getGenericType());
                TSField tsField = new TSField(field.getName(), tsInterface, fieldType);
                tsInterface.addTsField(tsField);
            }
        }

    }

    public void convertJsonTypeInfoAnnotation(Class javaType, TSInterface tsInterface) {
        JsonTypeInfo jsonTypeInfoAnnotation = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
        if(jsonTypeInfoAnnotation!=null){
            switch(jsonTypeInfoAnnotation.include()){

                case PROPERTY:
                case WRAPPER_OBJECT:
                case WRAPPER_ARRAY:
                    TSField tsField = new TSField(jsonTypeInfoAnnotation.property(), tsInterface, TypeMapper.map(String.class));
                    tsInterface.addTsField(tsField);
                    break;
                case EXTERNAL_PROPERTY:
                case EXISTING_PROPERTY:
                    break;
            }
        }
    }
}
