package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.converters.TypeMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSMap extends TSType {
    private TSType keyType = TypeMapper.tsString;
    private TSType valueType = TypeMapper.tsAny;

    public TSMap() {
        super("map");
    }

    public TSMap(TSType valueType) {
        this();
        this.valueType = valueType;
    }

    @Override
    public String getName() {
        return "{[key: " + keyType.getName() + "]: " + valueType.getName() +"}";
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(": " + getName());
    }
}
