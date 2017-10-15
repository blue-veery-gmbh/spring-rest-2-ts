package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.tsmodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by tomaszw on 31.07.2017.
 */
public interface ImplementationGenerator {
    void write(BufferedWriter writer, TSMethod method) throws IOException;
    TSType mapReturnType(TSMethod tsMethod, TSType tsType);
    SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType);
    List<TSParameter> getImplementationSpecificParameters(TSMethod method);

    List<TSDecorator> getDecorators(TSMethod tsMethod);

    List<TSDecorator> getDecorators(TSClass tsClass);

    void addComplexTypeUsage(TSClass tsClass);
}
