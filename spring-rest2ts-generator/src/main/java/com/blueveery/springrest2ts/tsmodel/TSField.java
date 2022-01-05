package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.converters.Property;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSField extends TSComplexTypeMember {
    private boolean optional;
    private boolean readOnly;
    private ILiteral initializationStatement;
    private Property sourceProperty;

    public TSField(String name, TSComplexElement owner, TSType type) {
        super(name, owner, type);
    }

    public TSField(String name, TSComplexElement owner, TSType type, Property sourceProperty) {
        super(name, owner, type);
        this.sourceProperty = sourceProperty;
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

    public Property getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(Property sourceProperty) {
        this.sourceProperty = sourceProperty;
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
