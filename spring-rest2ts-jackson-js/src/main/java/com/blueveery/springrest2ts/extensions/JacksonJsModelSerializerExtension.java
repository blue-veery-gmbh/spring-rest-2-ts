package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.convertToTypeLiteral;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.jacksonJSModule;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.wrapIntoTSLiteralArray;

public class JacksonJsModelSerializerExtension implements ModelSerializerExtension {
    private final TSClass objectMapperClass;
    private String objectMapperFieldName = "objectMapper";

    public JacksonJsModelSerializerExtension() {
        objectMapperClass = new TSClass("ObjectMapper", jacksonJSModule, new EmptyImplementationGenerator());
    }

    @Override
    public void addComplexTypeUsage(TSComplexElement tsComplexElement) {
        tsComplexElement.addScopedTypeUsage(objectMapperClass);
    }

    @Override
    public void addImplementationSpecificFields(TSComplexElement tsComplexElement) {
        if (tsComplexElement instanceof TSClass) {
            TSClass tsParentClass = (TSClass) tsComplexElement;
            while (tsParentClass.getExtendsClass() != null) {
                tsParentClass = tsParentClass.getExtendsClass().getReferencedType();
            }
            TSField objectMapperField = tsParentClass.getFieldByName(objectMapperFieldName);
            if (objectMapperField == null) {
                objectMapperField = new TSField(objectMapperFieldName, tsParentClass, objectMapperClass);
                objectMapperField.setReadOnly(true);
                objectMapperField.setInitializationStatement(new TSLiteral("", TypeMapper.tsAny, "new ObjectMapper()"));
                tsParentClass.addTsField(objectMapperField);
                tsParentClass.addScopedTypeUsage(objectMapperClass);
            }
        }
    }

    @Override
    public String generateSerializationCode(String modelVariableName, TSParameter tsParameter) {
        StringBuilder stringifyStatement = new StringBuilder();
        if (tsParameter.isOptional() || tsParameter.isNullable()) {
            stringifyStatement.append(modelVariableName);
            stringifyStatement.append(" && ");
        }
        return stringifyStatement.append("this.")
                .append(objectMapperFieldName)
                .append(".stringify<")
                .append(tsParameter.getType().getName())
                .append(">(")
                .append(modelVariableName)
                .append(")")
                .toString();
    }

    @Override
    public String generateDeserializationCode(String modelVariableName, TSMethod tsMethod) {
        try {
            StringBuilder parseStatement = new StringBuilder();
            if (tsMethod.isNullable()) {
                parseStatement.append(modelVariableName);
                parseStatement.append(" && ");
            }
            parseStatement.append("this.");
            parseStatement.append(objectMapperFieldName);
            parseStatement.append(".parse<");
            parseStatement.append(tsMethod.getType().getName());
            parseStatement.append(">(");
            parseStatement.append(modelVariableName);
            parseStatement.append(", {mainCreator: () => ");
            ILiteral iLiteral = wrapIntoTSLiteralArray(convertToTypeLiteral(tsMethod.getType()));
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