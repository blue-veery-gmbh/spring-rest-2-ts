package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;

public class TsTypeAlias extends TSScopedType {
    TSType aliasedType;

    public TsTypeAlias(String name, TSModule module, TSType aliasedType) {
        super(name, module);
        this.aliasedType = aliasedType;
    }

    public TSType getAliasedType() {
        return aliasedType;
    }

    @Override
    public void write(ImplementationGenerator implementationGenerator, BufferedWriter writer) throws IOException {
        writer.write("export type " + getName() + " = " );
        aliasedType.write(implementationGenerator, writer);
        writer.write(";" );
    }
}
