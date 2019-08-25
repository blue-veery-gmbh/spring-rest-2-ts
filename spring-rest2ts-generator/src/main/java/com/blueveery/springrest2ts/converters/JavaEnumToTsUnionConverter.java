package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;

/**
 * Created by tomek on 08.08.17.
 */
public class JavaEnumToTsUnionConverter extends ComplexTypeConverter {
    @Override

    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass, ClassNameMapper classNameMapper) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            TsUnion tsUnion = new TsUnion();
            String aliasName = classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName());
            TsTypeAlias tsTypeAlias = new TsTypeAlias(aliasName, tsModule, tsUnion);
            tsModule.addScopedType(tsTypeAlias);
            TypeMapper.registerTsType(javaClass, tsTypeAlias);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass) {
        TsTypeAlias tsTypeAlias = (TsTypeAlias) TypeMapper.map(javaClass);
        TsUnion tsUnion = (TsUnion) tsTypeAlias.getAliasedType();
        for (Object enumConstant : javaClass.getEnumConstants()) {
            String enumConstantStringValue = enumConstant.toString();
            TSLiteral tsLiteral = new TSLiteral(enumConstantStringValue, TypeMapper.tsString, enumConstantStringValue);
            tsUnion.getJoinedTsElementList().add(tsLiteral);
        }
    }
}
