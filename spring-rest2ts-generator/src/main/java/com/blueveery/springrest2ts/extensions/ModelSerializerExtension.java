package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.util.Collections;
import java.util.List;

public interface ModelSerializerExtension {
    void addComplexTypeUsage(TSComplexElement tsComplexElement);

    default void addImplementationSpecificFields(TSComplexElement tsComplexElement) {
    }

    default List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    default String generateSerializationCode(String modelVariableName) {
        throw new UnsupportedOperationException();
    }

    default String generateDeserializationCode(String modelVariableName) {
        throw new UnsupportedOperationException();
    }

    default String generateSerializationCode(String modelVariableName, TSParameter tsParameter) {
        return generateSerializationCode(modelVariableName);
    }

    default String generateDeserializationCode(String modelVariableName, TSMethod tsMethod) {
        return generateDeserializationCode(modelVariableName);
    }
}
