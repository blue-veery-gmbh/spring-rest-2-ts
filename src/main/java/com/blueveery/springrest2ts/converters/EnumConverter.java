package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSEnumConstant;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.util.Map;

/**
 * Created by tomek on 08.08.17.
 */
public class EnumConverter extends ComplexTypeConverter {
    @Override
    public void preConvert(Map<String, TSModule> modulesMap, Class javaClass) {
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny){
            TSModule tsModule = modulesMap.get(javaClass.getPackage().getName());
            TSEnum tsEnum = new TSEnum(javaClass.getSimpleName(), tsModule);
            tsModule.addScopedType(tsEnum);
            TypeMapper.registerTsType(javaClass, tsEnum);
        }
    }

    @Override
    public void convert(Map<String, TSModule> modulesMap, Class javaClass, ImplementationGenerator implementationGenerator) {
        TSEnum tsEnum = (TSEnum) TypeMapper.map(javaClass);
        for(Object enumConstant:javaClass.getEnumConstants()){
            tsEnum.getTsEnumConstantList().add(new TSEnumConstant(enumConstant.toString()));
        }
    }
}
