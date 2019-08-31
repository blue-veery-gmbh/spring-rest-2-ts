package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.converters.TypeMapper;

public interface INullableElement {
    TSType getType();
    void setType(TSType type);

    default void setNullable(boolean isNullable) {
        if (getType() instanceof TSUnion) {
            TSUnion tsUnion = (TSUnion) getType();
            boolean unionContainsNull = tsUnion.getJoinedTsElementList().stream().anyMatch(t -> t == TypeMapper.tsNull);
            if (isNullable) {
                if(!unionContainsNull){
                    tsUnion.getJoinedTsElementList().add(TypeMapper.tsNull);
                }
            }else{
                if(unionContainsNull){
                    tsUnion.getJoinedTsElementList().remove(TypeMapper.tsNull);
                }
            }
        }else{
            if (isNullable) {
                setType(new TSUnion(getType(), TypeMapper.tsNull));
            }
        }
    }

    default boolean isNullable(){
        if (getType() instanceof TSUnion) {
            TSUnion tsUnion = (TSUnion) getType();
            boolean unionContainsNull = tsUnion.getJoinedTsElementList().stream().anyMatch(t -> t == TypeMapper.tsNull);
            return unionContainsNull;
        }
        return false;
    }
}
