package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public interface IDecorated {
    List<TSDecorator> getTsDecoratorList();

    default void writeDecorators(BufferedWriter writer, List<TSDecorator> tsDecoratorList) throws IOException {
        for (TSDecorator tsDecorator : tsDecoratorList) {
            tsDecorator.write(writer);
            writer.newLine();
        }
    }
}
