package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSModule extends TSElement {
    private boolean isExternal;
    private Map<TSModule, TSImport> importMap = new TreeMap<>();
    private SortedSet<TSScopedType> scopedTypesSet = new TreeSet<>();

    public TSModule(String name) {
        this(name, false);
    }

    public TSModule(String tsModuleName, boolean isExternal) {
        super(tsModuleName);
        this.isExternal = isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public boolean isExternal() {
        return isExternal;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        for (TSImport tsImport:importMap.values()) {
            tsImport.write(generationContext, writer);
            writer.newLine();
        }

        writer.newLine();
        for (TSScopedType tsScopedType: scopedTypesSet) {
            tsScopedType.write(generationContext, writer);
            writer.newLine();
            writer.newLine();
        }
    }

    public void addScopedType(TSScopedType tsScopedType) {
        scopedTypesSet.add(tsScopedType);
    }

    public void scopedTypeUsage(TSScopedType tsScopedType) {
        TSModule module = tsScopedType.getModule();
        if(module != this){
            TSImport tsImport = importMap.get(module);
            if(tsImport == null){
                tsImport = new TSImport(module);
                importMap.put(module, tsImport);
            }
            tsImport.getWhat().add(tsScopedType);
        }
    }
}
