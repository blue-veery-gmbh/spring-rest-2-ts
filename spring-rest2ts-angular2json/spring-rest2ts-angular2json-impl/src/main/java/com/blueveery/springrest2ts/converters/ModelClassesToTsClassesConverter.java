package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithFormalTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.SortedSet;

public class ModelClassesToTsClassesConverter extends ModelClassesAbstractConverter {

    public ModelClassesToTsClassesConverter(
            ImplementationGenerator implementationGenerator, ObjectMapper objectMapper
    ) {
        super(implementationGenerator, objectMapper);
    }

    public ModelClassesToTsClassesConverter(
            ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper, ObjectMapper objectMapper
    ) {
        super(implementationGenerator, classNameMapper, objectMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                if (javaClass.isInterface()) {
                    TSInterface tsInterface = new TSInterface(createTsClassName(javaClass), tsModule);
                    tsModule.addScopedElement(tsInterface);
                    TypeMapper.registerTsType(javaClass, tsInterface);
                } else {
                    TSClass tsClass = new TSClass(createTsClassName(javaClass), tsModule, getImplementationGenerator());
                    tsModule.addScopedElement(tsClass);
                    TypeMapper.registerTsType(javaClass, tsClass);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void convertInheritance(Class javaClass) {
        TSType tsType = TypeMapper.map(javaClass);
        if (!javaClass.isInterface()) {
            TSClassReference tsClassReference = (TSClassReference) tsType;
            TSClass tsClass = tsClassReference.getReferencedType();

            TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
            TSClassReference tsSuperClassReference;
            if (superClass instanceof TSClassReference) {
                tsSuperClassReference = (TSClassReference) superClass;
            } else {
                tsSuperClassReference = getDefaultBaseClass();
            }

            if (tsSuperClassReference != null) {
                tsClass.setExtendsClass(tsSuperClassReference);
            }

            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType implementedInterface = TypeMapper.map(annotatedInterface.getType());
                if (implementedInterface instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) implementedInterface;
                    tsClass.addImplementsInterfaces(tsSuperClassInterface);
                }
            }
        } else {
            TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) tsType;
            TSInterface tsInterface = tsInterfaceReference.getReferencedType();
            for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
                TSType extendedInterface = TypeMapper.map(annotatedInterface.getType());
                if (extendedInterface instanceof TSInterfaceReference) {
                    TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) extendedInterface;
                    tsInterface.addExtendsInterfaces(tsSuperClassInterface);
                }
            }
        }
    }

    protected TSClassReference getDefaultBaseClass() {
        return null;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSParameterizedTypeReference<IParameterizedWithFormalTypes> typeReference = (TSParameterizedTypeReference<IParameterizedWithFormalTypes>) TypeMapper.map(javaClass);
        TSComplexElement tsComplexElement = (TSComplexElement) typeReference.getReferencedType();
        if (!tsComplexElement.isConverted()) {
            tsComplexElement.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), typeReference);
            SortedSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsComplexElement, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsComplexElement.addTsField(tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            for (TSField typeLevelSpecificField : objectMapper.addTypeLevelSpecificFields(javaClass, tsComplexElement)) {
                Property property = new Property(typeLevelSpecificField.getName(), 0);
                conversionListener.tsFieldCreated(property, typeLevelSpecificField);
            }
            tsComplexElement.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsComplexElement);
        }
    }
}
