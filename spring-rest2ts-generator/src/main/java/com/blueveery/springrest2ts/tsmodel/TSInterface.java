package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSInterface extends TSComplexType {
    SortedSet<TSInterface> extendsInterfaces = new TreeSet<>();

    public TSInterface(String name, TSModule module) {
        super(name, module);
    }

    public void addExtendsInterfaces(TSInterface tsInterface) {
        getModule().scopedTypeUsage(tsInterface);
        extendsInterfaces.add(tsInterface);
    }

    @Override
    public void write(ImplementationGenerator implementationGenerator, BufferedWriter writer) throws IOException {
        writer.write("export interface " + getName() + " ");

        if(!extendsInterfaces.isEmpty()){
            writer.write("extends  ");
            Iterator<TSInterface> iterator = extendsInterfaces.iterator();
            while (iterator.hasNext()){
                writer.write(iterator.next().getName());
                if(iterator.hasNext()){
                    writer.write(", ");
                }
            }
        }

        writer.write("{");
        writeMembers(implementationGenerator, writer);
        writer.write("}");
    }
}
