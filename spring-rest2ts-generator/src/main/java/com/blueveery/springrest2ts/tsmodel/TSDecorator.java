package com.blueveery.springrest2ts.tsmodel;


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
    public void write(BufferedWriter writer) throws IOException {
        writer.write("@");
        writer.write(tsFunction.getName());
        writer.write("(");
        for (int i = 0; i < tsLiteralList.size(); i++) {
            TSLiteral tsLiteral = tsLiteralList.get(i);
            tsLiteral.write(writer);
            if (i < tsLiteralList.size()-1) {
                writer.write(",");
            }
        }
        writer.write(")");
    }
}
