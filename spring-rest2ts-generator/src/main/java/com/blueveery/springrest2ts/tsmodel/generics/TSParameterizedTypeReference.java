package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSType;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class TSParameterizedTypeReference<T extends IParameterizedWithFormalTypes> extends TSType implements IParameterizedWithActualTypes {
    public T referencedType;
    private List<TSType> actualParameterList = new ArrayList<>();

    public TSParameterizedTypeReference(T referencedType) {
        super(referencedType.getName());
        this.referencedType = referencedType;
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
    public final void write(BufferedWriter writer){
        throw new UnsupportedOperationException();
    }
}
