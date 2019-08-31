package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 04.08.2017.
 */
public class TSArray extends TSType {
    private TSType elementType;

    public TSArray(TSType elementType) {
        super(elementType.getName()+"[]");
        this.elementType = elementType;
    }

    public TSType getElementType() {
        return elementType;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(": " + elementType.getName() + getName());
    }
}
