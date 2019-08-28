package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSParameterisedType extends TSType{

    private TSComplexType tsComplexType;
    private TSType tsTypeParameter;

    public TSParameterisedType(String name, TSComplexType tsComplexType, TSType tsTypeParameter) {
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
    public void write(ImplementationGenerator implementationGenerator, BufferedWriter writer) throws IOException {
        writer.write(getName());
    }
}
