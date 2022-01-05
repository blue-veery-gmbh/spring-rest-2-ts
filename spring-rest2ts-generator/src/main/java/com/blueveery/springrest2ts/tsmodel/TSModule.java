package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithFormalTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.implementation;
import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.typing;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSModule extends TSElement implements ICommentedElement {
    protected boolean isExternal;
    protected Map<TSModule, TSImport> importMap = new TreeMap<>();
    protected SortedSet<TSScopedElement> scopedTypesSet = new TreeSet<>();
    protected Path moduleRelativePath;
    protected ModuleExtensionType moduleExtensionType = typing;
    protected TSComment tsComment = new TSComment("ModuleComment");

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

    public SortedSet<TSScopedElement> getScopedTypesSet() {
        return scopedTypesSet;
    }

    public Map<TSModule, TSImport> getImportMap() {
        return importMap;
    }

    @Override
    public TSComment getTsComment() {
        return tsComment;
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
        this.writeImportBlock(writer);
        writer.newLine();
        tsComment.write(writer);
        this.writeScopedElements(writer);
    }

    protected void writeImportBlock(BufferedWriter writer) throws IOException {
        for (TSImport tsImport : importMap.values()) {
            tsImport.write(writer);
            writer.newLine();
        }
    }

    protected void writeScopedElements(BufferedWriter writer) throws IOException {
        for (TSScopedElement tsScopedElement : sort(scopedTypesSet)) {
            tsScopedElement.write(writer);
            writer.newLine();
            writer.newLine();
        }
    }

    protected List<? extends TSScopedElement> sort(SortedSet<TSScopedElement> scopedTypesSet) {
        List<TSScopedElement> sortedElements = new ArrayList<>();
        List<TSVariable> tsVariableList = new ArrayList<>();
        for (TSScopedElement tsScopedElement : scopedTypesSet) {
            if (tsScopedElement instanceof TSInterface) {
                sortedElements.add(tsScopedElement);
                continue;
            }
            if (tsScopedElement instanceof TSClass) {
                TSClass tsClass = (TSClass) tsScopedElement;
                addDependantClasses(tsClass, sortedElements);
                continue;
            }
            if (tsScopedElement instanceof TSVariable) {
                tsVariableList.add((TSVariable) tsScopedElement);
                continue;
            }
            sortedElements.add(tsScopedElement);

        }
        sortedElements.addAll(tsVariableList);
        return sortedElements;
    }

    private void addDependantClasses(TSClass tsClass, List<TSScopedElement> sortedElements) {
        TSClassReference extendsClassReference = tsClass.getExtendsClass();
        if (extendsClassReference != null) {
            TSClass tsBaseClass = extendsClassReference.getReferencedType();
            if (tsBaseClass.getModule() == this) {
                addDependantClasses(tsBaseClass, sortedElements);
            }
        }
        if (!sortedElements.contains(tsClass)) {
            sortedElements.add(tsClass);
        }
    }

    public void addScopedElement(TSScopedElement tsScopedElement) {
        scopedTypesSet.add(tsScopedElement);
        if (tsScopedElement instanceof TSClass || !Rest2tsGenerator.generateAmbientModules) {
            moduleExtensionType = implementation;
        }
    }

    public void scopedTypeUsage(TSType tsType) {
        if (tsType instanceof TSParameterizedTypeReference) {
            scopedTypeUsage(((TSParameterizedTypeReference) tsType));
        }
        if (tsType instanceof TSScopedElement) {
            scopedTypeUsage(((TSScopedElement) tsType));
        }
    }

    public void scopedTypeUsage(TSParameterizedTypeReference<?> typeReference) {
        IParameterizedWithFormalTypes referencedType = typeReference.getReferencedType();
        if (referencedType instanceof TSScopedElement) {
            TSScopedElement referencedScopedType = (TSScopedElement) referencedType;
            scopedTypeUsage(referencedScopedType);
        }
        if (referencedType instanceof TSFormalTypeParameter) {
            TSFormalTypeParameter formalTypeParameter = (TSFormalTypeParameter) referencedType;
            scopedTypeUsage(formalTypeParameter.getBoundTo());
        }
        for (TSType tsType : typeReference.getTsTypeParameterList()) {
            if (tsType instanceof TSParameterizedTypeReference) {
                scopedTypeUsage((TSParameterizedTypeReference<?>) tsType);
            }
        }
    }

    public void scopedTypeUsage(TSScopedElement tsScopedElement) {
        TSModule module = tsScopedElement.getModule();
        if (module != this && module != TypeMapper.systemModule) {
            TSImport tsImport = importMap.get(module);
            if (tsImport == null) {
                tsImport = new TSImport(module);
                importMap.put(module, tsImport);
            }
            tsImport.getWhat().add(tsScopedElement);
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
