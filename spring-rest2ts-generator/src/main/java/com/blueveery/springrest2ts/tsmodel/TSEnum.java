package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 08.08.17.
 */
public class TSEnum extends TSScopedElement {
    private List<TSEnumConstant> tsEnumConstantList = new ArrayList<>();

    public TSEnum(String name, TSModule module) {
        super(name, module);
    }

    public List<TSEnumConstant> getTsEnumConstantList() {
        return tsEnumConstantList;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        writer.write("export enum " + getName() + " {");
        writer.newLine();
        for(int i=0;i<tsEnumConstantList.size();i++){
            TSEnumConstant tsEnumConstant = tsEnumConstantList.get(i);
            tsEnumConstant.write(writer);
            if((i+1)<tsEnumConstantList.size()){
                writer.write(",");
            }
            writer.newLine();
        }

        writer.write("}");
    }
}
