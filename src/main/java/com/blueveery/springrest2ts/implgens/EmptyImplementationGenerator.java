package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

public class EmptyImplementationGenerator implements ImplementationGenerator {
    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        return tsType;
    }

    @Override
    public SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType) {
        return Collections.emptySortedSet();
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSMethod tsMethod) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSClass tsClass) {
        return Collections.emptyList();
    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        writer.write("//empty implementation generator");
    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {

    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {

    }

    @Override
    public void generateImplementationSpecificUtilTypes(GenerationContext generationContext, SortedMap<String, TSModule> tsModuleMap) {

    }
}
