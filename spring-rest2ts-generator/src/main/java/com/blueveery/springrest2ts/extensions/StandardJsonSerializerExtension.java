package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;

public class StandardJsonSerializerExtension implements ModelSerializerExtension {

    @Override
    public void addComplexTypeUsage(TSComplexElement tsComplexElement) {
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
