package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSTypeLiteral implements ILiteral {
    TSType tsType;

    public TSTypeLiteral(TSType tsType) {
        this.tsType = tsType;
    }

    public TSType getTsType() {
        return tsType;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(tsType.getName());
    }

    @Override
    public int hashCode() {
        return tsType.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof TSTypeLiteral) {
            return tsType.equals(((TSTypeLiteral) other).tsType);
        }
        return false;
    }

    @Override
    public String toString() {
        return tsType.getName();
    }
}
