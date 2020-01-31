package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSEnumConstant;
import com.blueveery.springrest2ts.tsmodel.TSModule;

/**
 * Created by tomek on 08.08.17.
 */
public class JavaEnumToTsEnumConverter extends ComplexTypeConverter {

    public JavaEnumToTsEnumConverter() {
        super(new EmptyImplementationGenerator());
    }

    public JavaEnumToTsEnumConverter(ClassNameMapper classNameMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
            TSEnum tsEnum = new TSEnum(classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName()), tsModule);
            tsModule.addScopedElement(tsEnum);
            TypeMapper.registerTsType(javaClass, tsEnum);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSEnum tsEnum = (TSEnum) TypeMapper.map(javaClass);
        for (Object enumConstant : javaClass.getEnumConstants()) {
            tsEnum.getTsEnumConstantList().add(new TSEnumConstant(((Enum) enumConstant).name()));
        }
        tsEnum.addAllAnnotations(javaClass.getAnnotations());
        conversionListener.tsScopedTypeCreated(javaClass, tsEnum);
    }
}
