package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.Collections;
import java.util.List;

public class StandardJsonSerializerExtension implements ModelSerializerExtension {

    @Override
    public void addComplexTypeUsage(TSComplexElement tsComplexElement) {
    }

    @Override
    public void addImplementationSpecificFields(TSComplexElement tsComplexElement) {
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    @Override
    public String generateSerializationCode(String modelVariableName) {
        return "JSON.stringify(" + modelVariableName + ")";
    }

    @Override
    public String generateDeserializationCode(String modelVariableName) {
        return "JSON.parse(" + modelVariableName + ")";
    }
}
