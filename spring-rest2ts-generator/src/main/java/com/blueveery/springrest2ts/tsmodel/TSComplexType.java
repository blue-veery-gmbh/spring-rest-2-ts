package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public abstract class TSComplexType extends TSScopedType implements IAnnotated, IDecorated {


    private SortedSet<TSField> tsFields = new TreeSet<>();
    private SortedSet<TSMethod> tsMethods = new TreeSet<>();
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private List<Annotation> annotationList = new ArrayList<>();
    private boolean converted;

    public TSComplexType(String name, TSModule module) {
        super(name, module);
    }


    public List<TSDecorator> getTsDecoratorList() {
        return tsDecoratorList;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public SortedSet<TSField> getTsFields() {
        return tsFields;
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
            if (tsArray.getElementType() instanceof TSScopedType) {
                TSScopedType tsScopedType = (TSScopedType) tsArray.getElementType();
                module.scopedTypeUsage(tsScopedType);
            }
        }
        if (tsType instanceof TSScopedType) {
            TSScopedType tsScopedType = (TSScopedType) tsType;
            module.scopedTypeUsage(tsScopedType);
        }
    }

    protected void writeMembers(ImplementationGenerator implementationGenerator, BufferedWriter writer) throws IOException {
        implementationGenerator.addImplementationSpecificFields(this);
        writeFields(implementationGenerator, writer, tsFields);
        SortedSet<TSField> implementationSpecificFields = implementationGenerator.getImplementationSpecificFields(this);
        writeFields(implementationGenerator, writer, implementationSpecificFields);

        if (!tsMethods.isEmpty()) {
            writer.newLine();
            writer.newLine();

            for (TSMethod tsMethod : tsMethods) {
                tsMethod.write(implementationGenerator, writer);
                writer.newLine();
                writer.newLine();
            }
        }

    }

    private void writeFields(ImplementationGenerator implementationGenerator, BufferedWriter writer, SortedSet<TSField> fieldList) throws IOException {
        if (!fieldList.isEmpty()) {
            writer.newLine();
            for (TSField tsField : fieldList) {
                tsField.write(implementationGenerator, writer);
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
