package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithFormalTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public abstract class TSComplexType extends TSScopedType implements IDecorated, IParameterizedWithFormalTypes {
    private SortedSet<TSField> tsFields = new TreeSet<>();
    private SortedSet<TSMethod> tsMethods = new TreeSet<>();
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private boolean converted;
    protected ImplementationGenerator implementationGenerator;
    private List<TSFormalTypeParameter> tsFormalTypeParameterList = new ArrayList<>();

    public TSComplexType(String name, TSModule module, ImplementationGenerator implementationGenerator) {
        super(name, module);
        this.implementationGenerator = implementationGenerator;
    }

    public List<TSDecorator> getTsDecoratorList() {
        return tsDecoratorList;
    }

    public SortedSet<TSField> getTsFields() {
        return tsFields;
    }

    @Override
    public List<TSFormalTypeParameter> getTsTypeParameterList() {
        return tsFormalTypeParameterList;
    }

    public void addTsMethod(TSMethod tsMethod) {
        addScopedTypeUsage(tsMethod.getType());
        tsMethod.getParameterList().forEach(p -> addScopedTypeUsage(p.getType()));
        tsMethods.add(tsMethod);
    }

    public void addTsField(TSField tsField) {
        addScopedTypeUsage(tsField.getType());
        tsFields.add(tsField);
    }

    public void addScopedTypeUsage(TSType tsType) {
        if (tsType instanceof TSArray) {
            TSArray tsArray = (TSArray) tsType;
            addScopedTypeUsage(tsArray.getElementType());
        }
        if (tsType instanceof TSParameterizedTypeReference){
            TSType referencedType = (TSType) ((TSParameterizedTypeReference) tsType).getReferencedType();
            addScopedTypeUsage(referencedType);
        }
        if (tsType instanceof TSScopedType) {
            TSScopedType tsScopedType = (TSScopedType) tsType;
            module.scopedTypeUsage(tsScopedType);
        }
    }

    protected void writeMembers(BufferedWriter writer) throws IOException {
        implementationGenerator.addImplementationSpecificFields(this);
        writeFields(writer, tsFields);
        SortedSet<TSField> implementationSpecificFields = implementationGenerator.getImplementationSpecificFields(this);
        writeFields(writer, implementationSpecificFields);

        if (!tsMethods.isEmpty()) {
            writer.newLine();
            writer.newLine();

            for (TSMethod tsMethod : tsMethods) {
                tsMethod.write(writer);
                writer.newLine();
                writer.newLine();
            }
        }

    }

    private void writeFields(BufferedWriter writer, SortedSet<TSField> fieldList) throws IOException {
        if (!fieldList.isEmpty()) {
            writer.newLine();
            for (TSField tsField : fieldList) {
                tsField.write(writer);
                writer.newLine();
            }
        }
    }

    public boolean isConverted() {
        return converted;
    }

    public void setConverted(boolean converted) {
        this.converted = converted;
    }
}
