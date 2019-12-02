package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassesToTsInterfacesConverter extends ComplexTypeConverter {
    private ObjectMapper objectMapper;

    public ModelClassesToTsInterfacesConverter(ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator());
        this.objectMapper = objectMapper;
    }

    public ModelClassesToTsInterfacesConverter(ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                TSInterface tsInterface = new TSInterface(createTsClassName(javaClass), tsModule);
                tsModule.addScopedType(tsInterface);
                TypeMapper.registerTsType(javaClass, tsInterface);
                return true;
            }
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) TypeMapper.map(javaClass);
        TSInterface tsInterface = tsInterfaceReference.getReferencedType();
        if (!tsInterface.isConverted()) {
            tsInterface.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), tsInterface);
            if (!javaClass.isInterface()) {
                if (javaClass.getSuperclass() != Object.class) {
                    TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
                    if (superClass instanceof TSInterfaceReference) {
                        TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
                        tsInterface.addExtendsInterfaces(tsSuperClassInterface);
                    }
                }
            }

            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType superClass = TypeMapper.map(annotatedInterface.getType());
                if (superClass instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
                    tsInterface.addExtendsInterfaces(tsSuperClassInterface);
                }
            }


            SortedSet<Property> propertySet = getClassProperties(javaClass);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsInterface, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsInterface.addTsField(tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            objectMapper.addTypeLevelSpecificFields(javaClass, tsInterface);
            tsInterface.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsInterface);
        }

    }

    private void setAsNullableType(Property property, TSField tsField, NullableTypesStrategy nullableTypesStrategy) {
        if (property.getGetterType() != null) {
            nullableTypesStrategy.setAsNullableType(property.getGetterType(), property.getDeclaredAnnotations(), tsField);
            return;
        }

        if (property.getSetterType() != null && Objects.equals(property.getGetterType(), property.getSetterType())) {
            nullableTypesStrategy.setAsNullableType(property.getSetterType(), property.getDeclaredAnnotations(), tsField);
        }
    }

    private SortedSet<Property> getClassProperties(Class javaClass) {
        Map<String, Property> propertyMap = new HashMap<>();
        int currentIndex = 0;

        for (Field field : javaClass.getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                if(objectMapper.filter(field)) {
                    Property property = new Property(objectMapper.getPropertyName(field), currentIndex++, field);
                    propertyMap.put(property.getName(), property);
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
                }

                if(couldBeSetter(method) && objectMapper.filter(method, false)){
                    String propertyName = objectMapper.getPropertyName(method, false);
                    final int newIndex = currentIndex++;
                    Property property = propertyMap.computeIfAbsent(propertyName, (key) -> new Property(key, newIndex));
                    property.setSetter(method);
                }

            }
        }

        return new TreeSet<>(propertyMap.values());
    }

    public boolean couldBeGetter(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    public boolean couldBeSetter(Method method) {
        return method.getParameterCount() == 1 && method.getReturnType() == void.class;
    }

}
