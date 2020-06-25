package com.blueveery.springrest2ts.extensions;

public class StandardJsonSerializerExtension implements ModelSerializerExtension {
    @Override
    public String generateSerializationCode(String modelVariableName) {
        return "JSON.stringify(" + modelVariableName + ")";
    }

    @Override
    public String generateDeserializationCode(String modelVariableName) {
        return "JSON.parse(" + modelVariableName + ")";
    }
}
