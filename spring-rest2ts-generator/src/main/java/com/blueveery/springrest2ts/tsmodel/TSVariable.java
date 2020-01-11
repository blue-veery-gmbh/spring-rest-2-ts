package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.converters.TypeMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSVariable extends TSScopedElement {
    TSDeclarationType declarationType;
    TSType tsType;
    ILiteral value;

    public TSVariable(String name, TSModule module, TSDeclarationType declarationType, TSType tsType, ILiteral value) {
        super(name, module);
        this.declarationType = declarationType;
        this.tsType = tsType;
        this.value = value;
    }

    public TSDeclarationType getDeclarationType() {
        return declarationType;
    }

    public TSType getTsType() {
        return tsType;
    }

    public ILiteral getValue() {
        return value;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.newLine();
        if (tsComment != null) {
            tsComment.write(writer);
        }
        writer.newLine();
        writer.write("export ");
        writer.write(declarationType.toString());
        writer.write("  ");
        writer.write(getName());
        if(tsType != null && tsType != TypeMapper.tsAny && tsType != TypeMapper.tsObject){
            writer.write(" : ");
            writer.write(tsType.getName());
        }
        if (value != null) {
            writer.write(" = ");
            value.write(writer);
        }
        writer.write(";");

    }
}
