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
import java.util.List;

/**
 * Created by tomaszw on 31.07.2017.
 */
public interface ImplementationGenerator {
    void setExtensions(List<? extends ConversionExtension> conversionExtensionSet);

    void setSerializationExtension(ModelSerializerExtension modelSerializerExtension);

    ModelSerializerExtension getSerializationExtension();

    void write(BufferedWriter writer, TSMethod method) throws IOException;

    default void changeMethodBeforeImplementationGeneration(TSMethod tsMethod) {

    }
    TSType mapReturnType(TSMethod tsMethod, TSType tsType);

    List<TSParameter> getImplementationSpecificParameters(TSMethod method);

    List<TSDecorator> getDecorators(TSMethod tsMethod);

    List<TSDecorator> getDecorators(TSClass tsClass);

    void addComplexTypeUsage(TSClass tsClass);

    void addImplementationSpecificFields(TSComplexElement tsComplexType);
}
