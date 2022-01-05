package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.converters.TypeMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

public class TSLiteral extends TSElement implements ILiteral{
    TSType tsType;
    String value;

    public TSLiteral(String name, TSType tsType, String value) {
        super(name);
        this.tsType = tsType;
        this.value = value;
    }

    public TSType getTsType() {
        return tsType;
    }

    public String getValue() {
        return value;
    }

    public String toTsValue(){
        StringBuilder stringValue = new StringBuilder();
        if(tsType == TypeMapper.tsString){
            stringValue.append("'");
        }
        stringValue.append(value);
        if(tsType == TypeMapper.tsString){
            stringValue.append("'");
        }
        return stringValue.toString();
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(toTsValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TSLiteral tsLiteral = (TSLiteral) o;
        return Objects.equals(tsType, tsLiteral.tsType) && Objects.equals(value, tsLiteral.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tsType, value);
    }
}
