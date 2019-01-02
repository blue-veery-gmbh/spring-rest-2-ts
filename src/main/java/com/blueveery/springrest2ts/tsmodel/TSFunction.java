package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSFunction extends TSScopedType{

    private List<TSParameter> parameterList = new ArrayList<>();

    public TSFunction(String name, TSModule module) {
        super(name, module);
    }

    public List<TSParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        throw new UnsupportedOperationException();
    }
}

