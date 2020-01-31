package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSFunction extends TSScopedElement {

    private List<TSParameter> parameterList = new ArrayList<>();

    public TSFunction(String name, TSModule module) {
        super(name, module);
    }

    public List<TSParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        throw new UnsupportedOperationException();
    }
}

