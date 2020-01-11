package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

@Deprecated()
public class TSParameterisedType extends TSType{

    private TSComplexElement tsComplexType;
    private TSType tsTypeParameter;

    public TSParameterisedType(String name, TSComplexElement tsComplexType, TSType tsTypeParameter) {
        super(name);
        this.tsComplexType = tsComplexType;
        this.tsTypeParameter = tsTypeParameter;
    }

    public TSType getTsTypeParameter() {
        return tsTypeParameter;
    }

    public void setTsTypeParameter(TSType tsTypeParameter) {
        this.tsTypeParameter = tsTypeParameter;
    }

    @Override
    public String getName() {
        return tsComplexType.getName() + "<" + tsTypeParameter.getName() + ">";
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(getName());
    }
}
