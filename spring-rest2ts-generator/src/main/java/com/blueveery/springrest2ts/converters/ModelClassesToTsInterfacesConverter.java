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
public class ModelClassesToTsInterfacesConverter extends ModelClassesAbstractConverter {

    public ModelClassesToTsInterfacesConverter(ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), objectMapper);
    }

    public ModelClassesToTsInterfacesConverter(ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper, objectMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
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
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) TypeMapper.map(javaClass);
        TSInterface tsInterface = tsInterfaceReference.getReferencedType();
        if (!tsInterface.isConverted()) {
            tsInterface.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), tsInterfaceReference);
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


            SortedSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

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

}
