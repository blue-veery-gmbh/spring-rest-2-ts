package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSField extends TSComplexTypeMember {
    private boolean optional;
    private boolean readOnly;
    private ILiteral initializationStatement;

    public TSField(String name, TSComplexElement owner, TSType type) {
        super(name, owner, type);
    }


    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public ILiteral getInitializationStatement() {
        return initializationStatement;
    }

    public void setInitializationStatement(ILiteral initializationStatement) {
        this.initializationStatement = initializationStatement;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        writeDecorators(writer, getTsDecoratorList());
        if(readOnly){
            writer.write("readonly ");
        }
        writer.write("  "+getName());
        TSType type = getType();
        if(type != null) {
            if (optional) {
                writer.write("?");
            }
            writer.write(": ");
            if (type instanceof TSArrowFuncType) {
                type.write(writer);
            } else {
                writer.write(type.getName());
            }
        }
        if (initializationStatement != null) {
            writer.write(" = ");
            initializationStatement.write(writer);
        }
        writer.write(";");
    }
}
