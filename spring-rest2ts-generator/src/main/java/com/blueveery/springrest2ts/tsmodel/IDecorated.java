package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public interface IDecorated {
    List<TSDecorator> getTsDecoratorList();

    default void writeDecorators(ImplementationGenerator implementationGenerator, BufferedWriter writer, List<TSDecorator> tsDecoratorList) throws IOException {
        for (TSDecorator tsDecorator : tsDecoratorList) {
            tsDecorator.write(implementationGenerator, writer);
            writer.newLine();
        }
    }
}
