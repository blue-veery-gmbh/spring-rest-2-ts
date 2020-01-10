package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassesToTsAngular2JsonApiConverter extends ModelClassesAbstractConverter {

    public ModelClassesToTsAngular2JsonApiConverter(ObjectMapper objectMapper) {
        this(new NoChangeClassNameMapper(), objectMapper);
    }

    public ModelClassesToTsAngular2JsonApiConverter(ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper, objectMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                TSClass tsClass = new TSClass(createTsClassName(javaClass), tsModule, getImplementationGenerator());
                tsModule.addScopedType(tsClass);
                TypeMapper.registerTsType(javaClass, tsClass);
                return true;
            }
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSClassReference tsClassReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsClassReference.getReferencedType();
        if (!tsClass.isConverted()) {
            tsClass.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), tsClassReference);
            if (!javaClass.isInterface()) {
                TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
                if (superClass instanceof TSClassReference) {
                    TSClassReference tsSuperClass = (TSClassReference) superClass;
                    tsClass.setExtendsClass(tsSuperClass);
                }
            }

            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType superClass = TypeMapper.map(annotatedInterface.getType());
                if (superClass instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
                    tsClass.add(tsSuperClassInterface);
                }
            }


            SortedSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsClass, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsClass.addTsField(tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            objectMapper.addTypeLevelSpecificFields(javaClass, tsClass);
            tsClass.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsClass);
        }

    }

}
