package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ModelClassesAbstractConverter extends ClassConverter<ModelConversionExtension>{
    protected ObjectMapper defaultObjectMapper;
    private Map<String, ObjectMapper> objectMapperMap = new HashMap<>();

    public ModelClassesAbstractConverter(ImplementationGenerator implementationGenerator, ObjectMapper objectMapper) {
        super(implementationGenerator);
        this.defaultObjectMapper = objectMapper;
    }

    public ModelClassesAbstractConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(implementationGenerator, classNameMapper);
        this.defaultObjectMapper = objectMapper;
    }

    public Map<String, ObjectMapper> getObjectMapperMap() {
        return objectMapperMap;
    }

    protected ObjectMapper selectObjectMapper(Class javaClass) {
        String packageName = javaClass.getPackage().getName();
        do{
            ObjectMapper objectMapper = objectMapperMap.get(packageName);
            if (objectMapper != null) {
                return objectMapper;
            }
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
        }while (packageName.contains("."));
        return defaultObjectMapper;
    }

    protected void setAsNullableType(Property property, TSField tsField, NullableTypesStrategy nullableTypesStrategy) {
        if (property.getGetterType() != null) {
            nullableTypesStrategy.setAsNullableType(property.getGetterType(), property.getDeclaredAnnotations(), tsField);
            return;
        }

        if (property.getSetterType() != null && !Objects.equals(property.getGetterType(), property.getSetterType())) {
            nullableTypesStrategy.setAsNullableType(property.getSetterType(), property.getDeclaredAnnotations(), tsField);
        }
    }

    protected SortedSet<Property> getClassProperties(Class javaClass, ObjectMapper objectMapper) {
        Map<String, Property> propertyMap = new HashMap<>();
        int currentIndex = 0;

        for (Field field : javaClass.getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                if(objectMapper.filter(field)) {
                    Property property = new Property(objectMapper.getPropertyName(field), currentIndex++, field);
                    propertyMap.put(property.getName(), property);
                    objectMapper.setIfIsIgnored(property, field);
                }
            }
        }

        for (Method method : javaClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                if(couldBeGetter(method) && objectMapper.filter(method, true)){
                    String propertyName = objectMapper.getPropertyName(method, true);
                    final int newIndex = currentIndex++;
                    Property property = propertyMap.computeIfAbsent(propertyName, (key) -> new Property(key, newIndex));
                    property.setGetter(method);
                    objectMapper.setIfIsIgnored(property, method);
                }

                if(couldBeSetter(method) && objectMapper.filter(method, false)){
                    String propertyName = objectMapper.getPropertyName(method, false);
                    final int newIndex = currentIndex++;
                    Property property = propertyMap.computeIfAbsent(propertyName, (key) -> new Property(key, newIndex));
                    property.setSetter(method);
                    objectMapper.setIfIsIgnored(property, method);
                }

            }
        }

        return new TreeSet<>(propertyMap.values());
    }

    private boolean couldBeGetter(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    private boolean couldBeSetter(Method method) {
        return method.getParameterCount() == 1 && method.getReturnType() == void.class;
    }
}
