package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.converters.TypeMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSLiteral extends TSElement implements ILiteral{
    TSType tsType;
    String value;

    public TSLiteral(String name, TSType tsType, String value) {
        super(name);
        this.tsType = tsType;
        this.value = value;
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
}
