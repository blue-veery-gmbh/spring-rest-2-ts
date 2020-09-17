package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.Collections;
import java.util.List;

public interface ModelSerializerExtension {
    void addComplexTypeUsage(TSComplexElement tsComplexElement);

    default void addImplementationSpecificFields(TSComplexElement tsComplexElement) {
    }

    default List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    String generateSerializationCode(String modelVariableName);

    String generateDeserializationCode(String modelVariableName);
}
