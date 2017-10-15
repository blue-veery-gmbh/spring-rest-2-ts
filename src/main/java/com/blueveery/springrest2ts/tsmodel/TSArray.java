package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

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

    @Override
    public void write(GenerationContext context, BufferedWriter writer) throws IOException {
        writer.write(": " + elementType.getName() + getName());
    }
}
