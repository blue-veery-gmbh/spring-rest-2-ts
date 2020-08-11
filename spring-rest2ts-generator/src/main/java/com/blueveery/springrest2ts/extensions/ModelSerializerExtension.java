package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.List;

public interface ModelSerializerExtension {
    void addComplexTypeUsage(TSComplexElement tsComplexElement);

    void addImplementationSpecificFields(TSComplexElement tsComplexElement);

    List<TSParameter> getImplementationSpecificParameters(TSMethod method);

    String generateSerializationCode(String modelVariableName);

    String generateDeserializationCode(String modelVariableName);
}
