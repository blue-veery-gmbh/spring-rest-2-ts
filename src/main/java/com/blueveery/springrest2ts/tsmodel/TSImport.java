package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSImport extends TSElement{
    private TSModule fromModule;
    private SortedSet<TSScopedType> what = new TreeSet<>();

    public TSModule getFromModule() {
        return fromModule;
    }

    public SortedSet<TSScopedType> getWhat() {
        return what;
    }

    public TSImport(TSModule fromModule) {
        super(fromModule.getName());
        this.fromModule = fromModule;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        StringBuilder whatString = new StringBuilder();
        int i=0;
        for(TSScopedType tsScopedType:getWhat()){
            whatString.append(tsScopedType.getName());
            if((i++)<getWhat().size()-1) {
                whatString.append(", ");
            }
        }
        String relativePath = fromModule.getModuleRelativePath() == null ? "" : fromModule.getModuleRelativePath() + "/";
        writer.write("import {" + whatString.toString() + "} from '" + relativePath + getFromModule().getName()+"';");
    }
}
