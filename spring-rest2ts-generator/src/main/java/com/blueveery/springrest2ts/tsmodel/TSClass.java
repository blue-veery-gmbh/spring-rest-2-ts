package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

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
    private TSClass extendsClass;
    private SortedSet<TSInterface> implementsInterfaces = new TreeSet<>();
    private boolean isAbstract;

    public TSClass(String name, TSModule module) {
        super(name, module);
        isAbstract = false;
    }

    public TSClass(String name, TSModule module, boolean isAbstract) {
        this(name, module);
        this.isAbstract = isAbstract;
    }

    public TSClass getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(TSClass extendsClass) {
        if(extendsClass!=null){
            module.scopedTypeUsage(extendsClass);
        }
        this.extendsClass = extendsClass;
    }

    public SortedSet<TSInterface> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        ImplementationGenerator implementationGenerator = generationContext.getImplementationGenerator(this);
        List<TSDecorator> decorators = implementationGenerator.getDecorators(this);
        writeDecorators(generationContext, writer, decorators);

        writer.write("export");
        if (isAbstract) {
            writer.write(" abstract");
        }
        writer.write(" class " + getName() + " ");

        if(extendsClass!=null){
            writer.write("extends " + extendsClass.getName() + " ");
        }

        if(!implementsInterfaces.isEmpty()){
            writer.write("implements ");
            Iterator<TSInterface> iterator = implementsInterfaces.iterator();
            while (iterator.hasNext()){
                writer.write(iterator.next().getName());
                if(iterator.hasNext()){
                    writer.write(", ");
                }
            }
        }

        writer.write("{");
        writeMembers(generationContext, writer);
        writer.write("}");
    }
}
