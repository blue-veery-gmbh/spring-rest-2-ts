package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.extensions.ConversionExtension;
import com.blueveery.springrest2ts.extensions.ModelSerializerExtension;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class EmptyImplementationGenerator implements ImplementationGenerator {
    @Override
    public void setExtensions(List<? extends ConversionExtension> conversionExtensionSet) {

    }

    @Override
    public void setSerializationExtension(ModelSerializerExtension modelSerializerExtension) {

    }

    @Override
    public ModelSerializerExtension getSerializationExtension() {
        return null;
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        return tsType;
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
    public void addImplementationSpecificFields(TSComplexElement tsComplexType) {

    }
}
