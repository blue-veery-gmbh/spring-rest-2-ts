package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.converters.TypeMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSLiteral extends TSElement{
    TSType tsType;
    String value;

    public TSLiteral(String name, TSType tsType, String value) {
        super(name);
        this.tsType = tsType;
        this.value = value;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        if(tsType == TypeMapper.tsString){
            writer.write("'");
        }
        writer.write(value);
        if(tsType == TypeMapper.tsString){
            writer.write("'");
        }
    }
}
