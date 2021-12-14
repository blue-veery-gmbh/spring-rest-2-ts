package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 31.07.2017.
 */
public class TSClass extends TSComplexElement {
    private TSClassReference extendsClass;
    private SortedSet<TSInterfaceReference> implementsInterfaces = new TreeSet<TSInterfaceReference>();
    private boolean isAbstract;

    public TSClass(String name, TSModule module, ImplementationGenerator implementationGenerator) {
        super(name, module, implementationGenerator);
        isAbstract = false;
    }

    public TSClass(
            String name, TSModule module,
            ImplementationGenerator implementationGenerator,
            TSFormalTypeParameter... formalTypeParameters
    ) {
        super(name, module, implementationGenerator, formalTypeParameters);
    }

    public TSClassReference getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(TSClassReference extendsClass) {
        if(extendsClass!=null){
            module.scopedTypeUsage(extendsClass);
        }
        this.extendsClass = extendsClass;
    }

    public SortedSet<TSInterfaceReference> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public void addImplementsInterfaces(TSInterfaceReference tsInterfaceReference) {
        getModule().scopedTypeUsage(tsInterfaceReference);
        implementsInterfaces.add(tsInterfaceReference);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public boolean isInstanceOf(TSComplexElement tsComplexType) {
        if (tsComplexType instanceof TSClass) {
            if (extendsClass == null) {
                return false;
            }
            if (extendsClass.getReferencedType() == tsComplexType) {
                return true;
            }

            return extendsClass.getReferencedType().isInstanceOf(tsComplexType);
        }
        for (TSInterfaceReference implementedInterface : implementsInterfaces) {
            if (implementedInterface.getReferencedType() == tsComplexType) {
                return true;
            }

            return implementedInterface.getReferencedType().isInstanceOf(tsComplexType);
        }
        return false;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        List<TSDecorator> decorators = implementationGenerator.getDecorators(this);
        writeDecorators(writer, decorators);
        writeDecorators(writer, getTsDecoratorList());

        writer.write("export");
        if (isAbstract) {
            writer.write(" abstract");
        }
        writer.write(" class " + getName() + " ");
        writer.write(typeParametersToString());
        if(extendsClass!=null){
            writer.write("extends " + extendsClass.getName() + " ");
        }

        if(!implementsInterfaces.isEmpty()){
            writer.write("implements ");
            Iterator<TSInterfaceReference> iterator = implementsInterfaces.iterator();
            while (iterator.hasNext()){
                writer.write(iterator.next().getName());
                if(iterator.hasNext()){
                    writer.write(", ");
                }
            }
        }

        writer.write("{");
        writeMembers(writer);
        writer.write("}");
    }
}
