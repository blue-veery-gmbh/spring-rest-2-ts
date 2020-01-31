package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

public class TSElementAlias extends TSScopedElement {
    TSType aliasedType;

    public TSElementAlias(String name, TSModule module, TSType aliasedType) {
        super(name, module);
        this.aliasedType = aliasedType;
    }

    public TSType getAliasedType() {
        return aliasedType;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        writer.write("export type " + getName() + " = " );
        aliasedType.write(writer);
        writer.write(";" );
    }
}
