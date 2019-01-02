package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TSArrowFuncType extends TSType {

    private List<TSParameter> parameterList = new ArrayList<>();
    private TSType returnType;

    public TSArrowFuncType(TSType returnType) {
        super("Function");
        this.returnType = returnType;
    }

    public TSArrowFuncType(List<TSParameter> parameterList, TSType returnType) {
        this(returnType);
        this.parameterList = parameterList;
    }

    public List<TSParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write("(");


        Iterator<TSParameter> iterator = parameterList.iterator();
        while (iterator.hasNext()) {
            iterator.next().write(generationContext, writer);
            if (iterator.hasNext()) {
                writer.write(", ");
            }
        }

        writer.write(") => ");
        writer.write(returnType.getName());
    }
}
