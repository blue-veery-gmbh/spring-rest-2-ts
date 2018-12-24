package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassToTsConverter extends ComplexTypeConverter {
    private ObjectMapper objectMapper;

    public ModelClassToTsConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

        objectMapper.addTypeLevelSpecificFields(javaType, tsInterface);


        for(Field field:javaType.getDeclaredFields()){
            if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())){
                if(objectMapper.filter(field, tsInterface)) {
                    List<TSField> tsFieldList = objectMapper.mapToField(field, tsInterface);
                    if(tsFieldList.size() == 1){
                        applyOptionalBaseOnType(field.getType(), tsFieldList.get(0));
                    }
                    tsFieldList.forEach(tsField -> tsInterface.addTsField(tsField));
                }
            }
        }

        for(Method method:javaType.getDeclaredMethods()){
            if(!Modifier.isStatic(method.getModifiers()) && isGetter(method)){
                if(objectMapper.filter(method, tsInterface)) {
                    List<TSField> tsFieldList = objectMapper.mapToField(method, tsInterface);
                    if(tsFieldList.size() == 1){
                        applyOptionalBaseOnType(method.getReturnType(), tsFieldList.get(0));
                    }
                    tsFieldList.forEach(tsField -> tsInterface.addTsField(tsField));
                }
            }
        }

    }

    private boolean isGetter(Method method) {
        if(method.getGenericParameterTypes().length != 0) {
            return false;
        }
        if(method.getReturnType() == Void.class){
            return false;
        }
        if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
            return false;
        }
        return true;
    }

    private void applyOptionalBaseOnType(Class propertyType, TSField tsField) {
        if(!tsField.isOptional()){
            if(Optional.class == propertyType){
                tsField.setOptional(true);
                return;
            }

            if(Number.class.isAssignableFrom(propertyType)){
                tsField.setOptional(true);
                return;
            }

            if(Boolean.class == propertyType){
                tsField.setOptional(true);
                return;
            }
        }
    }
}
