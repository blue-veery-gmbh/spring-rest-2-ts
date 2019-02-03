package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.converters.TypeMapper;

public interface INullableElement {
    TSType getType();
    void setType(TSType type);

    default void setNullable(boolean isNullable) {
        if (getType() instanceof TsUnion) {
            TsUnion tsUnion = (TsUnion) getType();
            boolean unionContainsNull = tsUnion.getJoinedTypeList().stream().anyMatch(t -> t == TypeMapper.tsNull);
            if (isNullable) {
                if(!unionContainsNull){
                    tsUnion.getJoinedTypeList().add(TypeMapper.tsNull);
                }
            }else{
                if(unionContainsNull){
                    tsUnion.getJoinedTypeList().remove(TypeMapper.tsNull);
                }
            }
        }else{
            if (isNullable) {
                setType(new TsUnion(getType(), TypeMapper.tsNull));
            }
        }
    }

    default boolean isNullable(){
        if (getType() instanceof TsUnion) {
            TsUnion tsUnion = (TsUnion) getType();
            boolean unionContainsNull = tsUnion.getJoinedTypeList().stream().anyMatch(t -> t == TypeMapper.tsNull);
            return unionContainsNull;
        }
        return false;
    }
}
