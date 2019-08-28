package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSMethod extends TSComplexTypeMember {
    private boolean isConstructor;
    private boolean isAbstract;
    private List<TSParameter> parameterList = new ArrayList<>();

    public TSMethod(String name, TSComplexType owner, TSType type, boolean isAbstract, boolean isConstructor) {
        super(name, owner, type);
        this.isAbstract = isAbstract;
        this.isConstructor = isConstructor;
        if(isConstructor){
            name = "constructor";
        }
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public List<TSParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void write(ImplementationGenerator implementationGenerator, BufferedWriter writer) throws IOException {
        List<TSDecorator> decorators = implementationGenerator.getDecorators(this);
        writeDecorators(implementationGenerator, writer, decorators);

        writer.write("public ");
        if(isAbstract) {
            writer.write("abstract ");
        }
        writer.write(getName());
        writer.write("(");
        List<TSParameter> totalTsParametersList = new ArrayList<>(parameterList);
        totalTsParametersList.addAll(implementationGenerator.getImplementationSpecificParameters(this));

        int counter = writeParameters(totalTsParametersList, implementationGenerator, writer, false, 0);
        writeParameters(totalTsParametersList, implementationGenerator, writer, true, counter);

        writer.write(")");
        if(!isConstructor) {
            writer.write(": ");
            TSType returnType = implementationGenerator.mapReturnType(this, getType());
            writer.write(returnType.getName());
        }
        if(!isAbstract){
            writer.write(" {");
            writer.newLine();
            implementationGenerator.write(writer, this);
            writer.newLine();
            writer.write("}");
        }else{
            writer.write(";");
        }
    }

    private int writeParameters(List<TSParameter> totalTsParametersList, ImplementationGenerator implementationGenerator, BufferedWriter writer, boolean writeOptional, int counter) throws IOException {
        for (int i = 0; i < totalTsParametersList.size(); i++) {
            TSParameter p = totalTsParametersList.get(i);
            boolean isOptional = p.isOptional() || p.getDefaultValue() != null;
            if ((writeOptional && isOptional) || (!writeOptional && !isOptional)) {
                p.write(implementationGenerator, writer);
                if(counter<totalTsParametersList.size()-1){
                    writer.write(", ");
                }
                counter++;
            }
        }
        return counter;
    }
}
