package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSArrowFunctionLiteral implements ILiteral {
    private final ILiteral returnValue;

    public TSArrowFunctionLiteral(ILiteral returnValue) {
        this.returnValue = returnValue;
    }

    public ILiteral getReturnValue() {
        return returnValue;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.append("() => ");
        returnValue.write(writer);
    }
}
