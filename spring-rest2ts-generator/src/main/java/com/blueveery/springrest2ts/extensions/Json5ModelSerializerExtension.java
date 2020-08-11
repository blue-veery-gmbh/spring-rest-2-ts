package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.Collections;
import java.util.List;

public class Json5ModelSerializerExtension implements ModelSerializerExtension {
    TSClass json5Type;

    public Json5ModelSerializerExtension() {
        TSModule tsModule = new TSModule("json5", null, true);
        json5Type = new TSClass("JSON5", tsModule, new EmptyImplementationGenerator());
    }

    @Override
    public void addComplexTypeUsage(TSComplexElement tsComplexElement) {
        tsComplexElement.addScopedTypeUsage(json5Type);
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
        return "JSON5.stringify(" + modelVariableName + ")";
    }

    @Override
    public String generateDeserializationCode(String modelVariableName) {
        return "JSON5.parse(" + modelVariableName + ")";
    }
}
