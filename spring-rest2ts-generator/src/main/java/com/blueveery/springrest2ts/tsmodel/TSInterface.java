package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSInterface extends TSComplexElement {
    SortedSet<TSInterfaceReference> extendsInterfaces = new TreeSet<TSInterfaceReference>();

    public TSInterface(String name, TSModule module) {
        super(name, module, new EmptyImplementationGenerator());
    }

    public void addExtendsInterfaces(TSInterfaceReference tsInterface) {
        getModule().scopedTypeUsage(tsInterface);
        extendsInterfaces.add(tsInterface);
    }

    @Override
    public boolean isInstanceOf(TSComplexElement tsComplexType) {
        for (TSInterfaceReference extendedInterface : extendsInterfaces) {
            if (extendedInterface.getReferencedType() == tsComplexType) {
                return true;
            }

            return extendedInterface.getReferencedType().isInstanceOf(tsComplexType);
        }
        return false;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        writeDecorators(writer, getTsDecoratorList());
        writer.write("export interface " + getName() + " ");
        writer.write(typeParametersToString());
        if(!extendsInterfaces.isEmpty()){
            writer.write("extends  ");
            Iterator<TSInterfaceReference> iterator = extendsInterfaces.iterator();
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
