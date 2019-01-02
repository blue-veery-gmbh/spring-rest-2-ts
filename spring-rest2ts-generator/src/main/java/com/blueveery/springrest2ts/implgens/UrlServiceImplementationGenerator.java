package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.converters.ModuleConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class UrlServiceImplementationGenerator implements ImplementationGenerator {
    private TSDecorator injectableDecorator;
    private TSDecorator injectDecorator;

    private Map<String, TSField> implementationSpecificFieldsMap;

    private final String FIELD_NAME_BACKEND_URL = "backendUrl";

    public UrlServiceImplementationGenerator() {
        TSModule angularCoreModule = new TSModule("@angular/core", null,true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));
        injectDecorator = new TSDecorator("", new TSFunction("Inject", angularCoreModule));
        injectDecorator.getTsLiteralList().add(new TSLiteral("", TypeMapper.tsString, "BACKEND_URL"));
    }

    @Override
    public void generateImplementationSpecificUtilTypes(GenerationContext generationContext, ModuleConverter tsModuleMap) {

    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (method.isConstructor()) {
            for (TSField field: implementationSpecificFieldsMap.values()) {
                writer.write("this." + field.getName() + " = " + field.getName() + ";");
                writer.newLine();
            }
        } else if (method.getName().compareTo("getBackendUrl") == 0) {
            writer.write("return this." + FIELD_NAME_BACKEND_URL + ";");
            writer.newLine();
        }
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if (tsMethod.getName().compareTo("getBackendUrl") == 0) {
            return TypeMapper.tsString;
        }
        return null;
    }

    @Override
    public SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType) {
        SortedSet<TSField> fieldsSet = new TreeSet<>();
        fieldsSet.addAll(implementationSpecificFieldsMap.values());
        return fieldsSet;
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        if (method.isConstructor()) {
            TSParameter backendUrlParameter = new TSParameter(FIELD_NAME_BACKEND_URL, TypeMapper.tsString);
            backendUrlParameter.getTsDecoratorList().add(injectDecorator);
            List<TSParameter> tsParameters = new ArrayList<>();
            tsParameters.add(backendUrlParameter);
            return tsParameters;
        }
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSMethod tsMethod) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSClass tsClass) {
        return Collections.singletonList(injectableDecorator);
    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {
        tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
        tsClass.addScopedTypeUsage(injectDecorator.getTsFunction());
    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {
        implementationSpecificFieldsMap = new TreeMap<>();
        implementationSpecificFieldsMap.put(FIELD_NAME_BACKEND_URL, new TSField(FIELD_NAME_BACKEND_URL, tsComplexType, new TSSimpleType("string")));
    }
}
