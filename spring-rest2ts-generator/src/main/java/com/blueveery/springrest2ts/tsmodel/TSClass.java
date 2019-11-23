package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
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
public class TSClass extends TSComplexType {
    private TSClassReference extendsClass;
    private SortedSet<TSInterfaceReference> implementsInterfaces = new TreeSet<TSInterfaceReference>();
    private boolean isAbstract;

    public TSClass(String name, TSModule module, ImplementationGenerator implementationGenerator) {
        super(name, module, implementationGenerator);
        isAbstract = false;
    }


    public TSClassReference getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(TSClassReference extendsClass) {
        if(extendsClass!=null){
            module.scopedTypeUsage(extendsClass.getReferencedType());
        }
        this.extendsClass = extendsClass;
    }

    public SortedSet<TSInterfaceReference> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        List<TSDecorator> decorators = implementationGenerator.getDecorators(this);
        writeDecorators(writer, decorators);

        writer.write("export");
        if (isAbstract) {
            writer.write(" abstract");
        }
        writer.write(" class " + getName() + " ");

        if(extendsClass!=null){
            writer.write("extends " + extendsClass.getName() + " ");
        }
        writer.write(typeParametersToString());
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
