package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.List;

public interface ModelSerializerExtension {
    List<TSParameter> getImplementationSpecificParameters(TSMethod method);
    String generateSerializationCode(String modelVariableName);
    String generateDeserializationCode(String modelVariableName);
}
