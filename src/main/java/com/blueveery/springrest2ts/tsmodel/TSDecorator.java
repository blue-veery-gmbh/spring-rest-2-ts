package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSDecorator extends TSElement {
    private TSFunction tsFunction;
    private List<TSLiteral> tsLiteralList = new ArrayList<>();


    public TSDecorator(String name, TSFunction tsFunction) {
        super(name);
        this.tsFunction = tsFunction;
    }

    public TSFunction getTsFunction() {
        return tsFunction;
    }

    public List<TSLiteral> getTsLiteralList() {
        return tsLiteralList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write("@");
        writer.write(tsFunction.getName());
        writer.write("(");
        for (TSLiteral tsLiteral : tsLiteralList) {
            tsLiteral.write(generationContext, writer);
            writer.write(".");
        }
        writer.write(")");
    }
}
