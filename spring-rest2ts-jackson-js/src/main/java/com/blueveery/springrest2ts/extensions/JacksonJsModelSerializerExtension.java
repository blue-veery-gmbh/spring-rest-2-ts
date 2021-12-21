package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.convertToTypeLiteral;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.jacksonJSModule;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.wrapIntoTSLiteralArray;

public class JacksonJsModelSerializerExtension implements ModelSerializerExtension {
    private final TSClass objectMapper;

    public JacksonJsModelSerializerExtension() {
        objectMapper = new TSClass("ObjectMapper", jacksonJSModule, new EmptyImplementationGenerator());
    }

    @Override
    public void addComplexTypeUsage(TSComplexElement tsComplexElement) {
        tsComplexElement.addScopedTypeUsage(objectMapper);
    }

    @Override
    public String generateSerializationCode(String modelVariableName, TSType returnType) {
        StringBuilder stringifyStatement = new StringBuilder();
        stringifyStatement.append("objectMapper.stringify<");
        stringifyStatement.append(returnType.getName());
        stringifyStatement.append(">(");
        stringifyStatement.append(modelVariableName);
        stringifyStatement.append(")");
        return stringifyStatement.toString();
    }

    @Override
    public String generateDeserializationCode(String modelVariableName, TSType returnType) {
        try {
            StringBuilder parseStatement = new StringBuilder();
            parseStatement.append("objectMapper.parse<");
            parseStatement.append(returnType.getName());
            parseStatement.append(">(");
            parseStatement.append(modelVariableName);
            parseStatement.append(", {mainCreator: () => ");
            ILiteral iLiteral = wrapIntoTSLiteralArray(convertToTypeLiteral(returnType));
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(stringWriter);
            iLiteral.write(writer);
            writer.flush();
            parseStatement.append(stringWriter);
            parseStatement.append("})");
            return parseStatement.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
