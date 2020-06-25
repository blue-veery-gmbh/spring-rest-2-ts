package com.blueveery.springrest2ts.extensions;

public interface ModelSerializerExtension {
    String generateSerializationCode(String modelVariableName);
    String generateDeserializationCode(String modelVariableName);
}
