package com.blueveery.springrest2ts.tsmodel;



import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.blueveery.springrest2ts.converters.TypeMapper;

import org.slf4j.Logger;

import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.implementation;
import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.typing;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSModule extends TSElement {
    private boolean isExternal = false;
    private Map<TSModule, TSImport> importMap = new TreeMap<>();
    private SortedSet<TSScopedType> scopedTypesSet = new TreeSet<>();
    private Path moduleRelativePath;
    private ModuleExtensionType moduleExtensionType = typing;

    public TSModule(String name, Path moduleRelativePath, boolean isExternal) {
        super(name);
        this.moduleRelativePath = moduleRelativePath;
        this.isExternal = isExternal;
    }

    public Path getModuleRelativePath() {
        return moduleRelativePath;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void writeModule(Path outputDir, Logger logger) throws IOException {
        Path tsModuleDir = outputDir.resolve(moduleRelativePath);
        Files.createDirectories(tsModuleDir);
        Path tsModuleFile = tsModuleDir.resolve(getName() + "." + moduleExtensionType);
        logger.info(String.format("Generating module into %s", tsModuleFile.toAbsolutePath().normalize().toUri()));
        BufferedWriter writer = Files.newBufferedWriter(tsModuleFile);
        write(writer);
        writer.close();
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        for (TSImport tsImport:importMap.values()) {
            tsImport.write(writer);
            writer.newLine();
        }

        writer.newLine();
        for (TSScopedType tsScopedType: scopedTypesSet) {
            tsScopedType.write(writer);
            writer.newLine();
            writer.newLine();
        }
    }

    public void addScopedType(TSScopedType tsScopedType) {
        scopedTypesSet.add(tsScopedType);
        if(tsScopedType instanceof TSClass){
            moduleExtensionType = implementation;
        }
    }

    public void scopedTypeUsage(TSScopedType tsScopedType) {
        TSModule module = tsScopedType.getModule();
        if(module != this && module != TypeMapper.systemModule){
            TSImport tsImport = importMap.get(module);
            if(tsImport == null){
                tsImport = new TSImport(module);
                importMap.put(module, tsImport);
            }
            tsImport.getWhat().add(tsScopedType);
        }
    }

    @Override
    public int hashCode() {
        return moduleRelativePath != null ? getName().hashCode() : moduleRelativePath.resolve(getName()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TSModule)) {
            return false;
        }
        TSModule otherTsModule = (TSModule) object;
        return getName().equals(otherTsModule.getName()) && Objects.equals(moduleRelativePath, otherTsModule.moduleRelativePath);
    }


}
