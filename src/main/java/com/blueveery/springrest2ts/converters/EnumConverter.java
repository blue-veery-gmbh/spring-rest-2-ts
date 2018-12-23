package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSEnumConstant;
import com.blueveery.springrest2ts.tsmodel.TSModule;

/**
 * Created by tomek on 08.08.17.
 */
public class EnumConverter extends ComplexTypeConverter {
    @Override
    public void preConvert(ModuleConverter moduleConverter, Class javaClass) {
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny){
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            TSEnum tsEnum = new TSEnum(javaClass.getSimpleName(), tsModule);
            tsModule.addScopedType(tsEnum);
            TypeMapper.registerTsType(javaClass, tsEnum);
        }
    }

    @Override
    public void convert(ModuleConverter moduleConverter, GenerationContext generationContext, Class javaClass) {
        TSEnum tsEnum = (TSEnum) TypeMapper.map(javaClass);
        for(Object enumConstant:javaClass.getEnumConstants()){
            tsEnum.getTsEnumConstantList().add(new TSEnumConstant(enumConstant.toString()));
        }
    }
}
