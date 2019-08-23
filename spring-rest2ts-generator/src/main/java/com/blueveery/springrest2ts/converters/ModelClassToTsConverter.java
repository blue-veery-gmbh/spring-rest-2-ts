package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.ClassNameMapper;
import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassToTsConverter extends ComplexTypeConverter {
    private ObjectMapper objectMapper;
    private GenerationContext generationContext;

    public ModelClassToTsConverter(ObjectMapper objectMapper, GenerationContext generationContext) {
        this.objectMapper = objectMapper;
        this.generationContext = generationContext;
    }

    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass, ClassNameMapper classNameMapper) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = moduleConverter.getTsModule(javaClass);
                TSInterface tsInterface = new TSInterface(classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName()), tsModule);
                tsModule.addScopedType(tsInterface);
                TypeMapper.registerTsType(javaClass, tsInterface);
                return true;
            }
        }
        return false;
    }

    @Override
    public void convert(Class javaClass) {
        TSInterface tsInterface = (TSInterface) TypeMapper.map(javaClass);
        if (!tsInterface.isConverted()) {
            tsInterface.setConverted(true);
            if (javaClass.getSuperclass() != Object.class) {
                TSType superClass = TypeMapper.map(javaClass.getSuperclass());
                if (superClass != TypeMapper.tsAny) {
                    tsInterface.addExtendsInterfaces((TSInterface) superClass);
                }
            }

            Map<String, Property> propertyMap = getClassProperties(javaClass);

            for (Property property : propertyMap.values()) {
                List<TSField> tsFieldList = objectMapper.mapToField(property, tsInterface, this);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0));
                }
                tsFieldList.forEach(tsField -> tsInterface.addTsField(tsField));
            }


            objectMapper.addTypeLevelSpecificFields(javaClass, tsInterface);
        }

    }

    private void setAsNullableType(Property property, TSField tsField) {
        if (property.getField() != null) {
            setAsNullableType(property.getField().getType(), property.getField().getDeclaredAnnotations(), tsField);
            return;
        }

        if (property.getGetter() != null) {
            setAsNullableType(property.getGetter().getReturnType(), property.getGetter().getDeclaredAnnotations(), tsField);
        }

    }

    private Map<String, Property> getClassProperties(Class javaClass) {
        Map<String, Property> propertyMap = new HashMap<>();

        for (Field field : javaClass.getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                if(objectMapper.filter(field)) {
                    Property property = new Property(objectMapper.getPropertyName(field), field);
                    propertyMap.put(property.getName(), property);
                }
            }
        }

        for (Method method : javaClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                if(couldBeGetter(method) && objectMapper.filter(method, true)){
                    String propertyName = objectMapper.getPropertyName(method, true);
                    Property property = propertyMap.computeIfAbsent(propertyName, (key) -> new Property(key));
                    property.setGetter(method);
                }

                if(couldBeSetter(method) && objectMapper.filter(method, false)){
                    String propertyName = objectMapper.getPropertyName(method, false);
                    Property property = propertyMap.computeIfAbsent(propertyName, (key) -> new Property(key));
                    property.setSetter(method);
                }

            }
        }

        return propertyMap;
    }

    public boolean couldBeGetter(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    public boolean couldBeSetter(Method method) {
        return method.getParameterCount() == 1 && method.getReturnType() == void.class;
    }

}
