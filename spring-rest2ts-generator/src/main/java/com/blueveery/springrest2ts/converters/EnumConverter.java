package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSEnumConstant;
import com.blueveery.springrest2ts.tsmodel.TSModule;

/**
 * Created by tomek on 08.08.17.
 */
public class EnumConverter extends ComplexTypeConverter {
    @Override

    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass, ClassNameMapper classNameMapper) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            TSEnum tsEnum = new TSEnum(classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName()), tsModule);
            tsModule.addScopedType(tsEnum);
            TypeMapper.registerTsType(javaClass, tsEnum);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass) {
        TSEnum tsEnum = (TSEnum) TypeMapper.map(javaClass);
        for (Object enumConstant : javaClass.getEnumConstants()) {
            tsEnum.getTsEnumConstantList().add(new TSEnumConstant(((Enum) enumConstant).name()));
        }
    }
}
