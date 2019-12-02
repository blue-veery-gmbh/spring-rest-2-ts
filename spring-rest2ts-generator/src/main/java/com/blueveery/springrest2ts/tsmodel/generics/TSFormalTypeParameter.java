package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSType;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSFormalTypeParameter extends TSType {
    private TSType boundTo;

    public TSFormalTypeParameter(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return super.getName() + (boundTo != null ?  " extends " + boundTo.getName() : "");
    }

    public TSType getBoundTo() {
        return boundTo;
    }

    public void setBoundTo(TSType boundTo) {
        this.boundTo = boundTo;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(getName());
        if (boundTo != null) {
            writer.write(" extends ");
            writer.write(boundTo.getName());
        }
    }
}
