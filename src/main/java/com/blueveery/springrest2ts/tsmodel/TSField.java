package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSField extends TSComplexTypeMember {

    public TSField(String name, TSComplexType owner, TSType type) {
        super(name, owner, type);
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write(getName());
        TSType type = getType();
        if(type != null) {
            writer.write(": ");
            if (type instanceof TSArrowFuncType) {
                type.write(generationContext, writer);
            } else {
                writer.write(type.getName());
            }
        }
        writer.write(";");
    }
}
