package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 08.08.17.
 */
public class TSEnum extends TSScopedType {
    private List<TSEnumConstant> tsEnumConstantList = new ArrayList<>();

    public TSEnum(String name, TSModule module) {
        super(name, module);
    }

    public List<TSEnumConstant> getTsEnumConstantList() {
        return tsEnumConstantList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write("export enum " + getName() + " {");
        writer.newLine();
        for(int i=0;i<tsEnumConstantList.size();i++){
            TSEnumConstant tsEnumConstant = tsEnumConstantList.get(i);
            tsEnumConstant.write(generationContext, writer);
            if((i+1)<tsEnumConstantList.size()){
                writer.write(",");
            }
            writer.newLine();
        }

        writer.write("}");
    }
}
