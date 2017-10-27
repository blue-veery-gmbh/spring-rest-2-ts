package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.*;

public class ErrorHandlerServiceImplementationGenerator implements ImplementationGenerator {
    private TSDecorator injectableDecorator;
    private TSDecorator injectDecorator;

    public ErrorHandlerServiceImplementationGenerator() {
        TSModule angularCoreModule = new TSModule("@angular/core");
        angularCoreModule.setExternal(true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));
        injectDecorator = new TSDecorator("", new TSFunction("Inject", angularCoreModule));
        injectDecorator.getTsLiteralList().add(new TSLiteral("", TypeMapper.tsString, "BACKEND_URL"));

    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (method.isConstructor()) {
            if (method.getOwner().getName().compareTo("DefaultErrorHandlerService") == 0) {
                writer.write("this.handlers = [];");
                writer.newLine();
            } else if (method.getOwner().getName().compareTo("SimpleErrorHandler") == 0) {
                for (TSField field : method.getOwner().getTsFields()) {
                    writer.write("this." + field.getName() + " = " + field.getName() + ";");
                    writer.newLine();
                }
            }
        } else if (method.getName().compareTo("handleErrorIfPresent") == 0) {
            writer.write("if (this.condition.isMetForGivenResponse(response)) {");
            writer.write("this.action();");
            writer.write("}");
            writer.newLine();
        } else if (method.getName().compareTo("handleErrorsIfPresent") == 0) {
            writer.write("for (const handler of this.handlers) {");
            writer.write("handler.handleErrorIfPresent(response);");
            writer.write("}");
            writer.newLine();
        } else if (method.getName().compareTo("addHandler") == 0) {
            writer.write("if (this.handlers.findIndex((val) => JSON.stringify(val) === JSON.stringify(errorHandler)) === -1) {this.handlers.push(errorHandler); }");
            writer.newLine();
        }
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        return TypeMapper.tsVoid;
    }

    @Override
    public SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType) {
        return Collections.emptySortedSet();
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSMethod tsMethod) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSClass tsClass) {
        if (tsClass.getName().compareTo("DefaultErrorHandlerService") == 0) {
            return Collections.singletonList(injectableDecorator);
        }
        return Collections.emptyList();
    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {
        tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
        tsClass.addScopedTypeUsage(injectDecorator.getTsFunction());
    }

    @Override
    public void setupCustom(TSComplexType tsComplexType) {
    }
}
