package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSType;

import java.io.BufferedWriter;
import java.util.Collections;
import java.util.List;

public abstract class TSParameterizedTypeReference<T extends IParameterizedWithFormalTypes> extends TSType implements IParameterizedWithActualTypes {
    private T referencedType;
    private List<TSType> actualParameterList;

    public T getReferencedType() {
        return referencedType;
    }

    public TSParameterizedTypeReference(T referencedType, List<TSType> actualParameterList) {
        super(referencedType.getName());
        this.referencedType = referencedType;
        this.actualParameterList = actualParameterList;
    }

    public TSParameterizedTypeReference(T referencedType, TSType actualParameter) {
        this(referencedType, Collections.singletonList(actualParameter));
    }

    @Override
    public List<TSType> getTsTypeParameterList() {
        return actualParameterList;
    }

    @Override
    public String getName() {
        StringBuilder name = new StringBuilder(referencedType.getName());
        name.append(this.typeParametersToString());
        return name.toString();
    }

    @Override
    public final void write(BufferedWriter writer) {
        throw new UnsupportedOperationException();
    }
}
