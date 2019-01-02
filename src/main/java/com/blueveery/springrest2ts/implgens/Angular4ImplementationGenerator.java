package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.converters.ModuleConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Angular4ImplementationGenerator implements ImplementationGenerator {
    private TSDecorator injectableDecorator;
    private TSClass observableClass;
    private TSClass httpClass;
    private TSClass responseClass;
    private TSClass requestOptionsClass;
    private TSClass headersClass;
    private TSClass urlServiceClass;
    private TSClass errorHandlerServiceClass;
    private TSClass subjectClass;
    private TSClass jsonScope;
    private TSClass jsonScopedSerializer;
    private TSClass jsonParser;

    private Set<TSField> implementationSpecificFieldsSet;

    private final String FIELD_NAME_HTTP_SERVICE = "httpService";
    private final String FIELD_NAME_URL_SERVICE = "urlService";
    private final String FIELD_NAME_ERROR_HANDLER_SERVICE = "errorHandlerService";
    private final String FIELD_NAME_SUBJECT = "subject";

    public Angular4ImplementationGenerator() {
        TSModule angularCoreModule = new TSModule("@angular/core", null,true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));

        TSModule rxjsObservableModule = new TSModule("rxjs/Observable", null,true);
        observableClass = new TSClass("Observable", rxjsObservableModule);

        TSModule angularHttpModule = new TSModule("@angular/http", null,true);
        httpClass = new TSClass("Http", angularHttpModule);
        responseClass = new TSClass("Response", angularHttpModule);
        requestOptionsClass = new TSClass("RequestOptionsArgs", angularHttpModule);
        headersClass = new TSClass("Headers", angularHttpModule);

        TSModule urlServiceModule = new TSModule("url.service", Paths.get("shared"), false);
        urlServiceClass = new TSClass("UrlService", urlServiceModule);

        TSModule errorHandlerServiceModule = new TSModule("default-error-handler.service", Paths.get("error-handling"), false);
        errorHandlerServiceClass = new TSClass("DefaultErrorHandlerService", errorHandlerServiceModule);

        TSModule subjectModule = new TSModule("rxjs/Subject", null,true);
        subjectClass = new TSClass("Subject", subjectModule);

        Path commonsPath = Paths.get("commons");
        TSModule jsonScopeModule = new TSModule("jsonScope", commonsPath, true);
        jsonScope = new TSClass("JsonScope", jsonScopeModule);

        TSModule jsonScopedSerializerModule = new TSModule("jsonScopedSerializer", commonsPath, true);
        jsonScopedSerializer = new TSClass("JsonScopedSerializer", jsonScopedSerializerModule);

        TSModule bvJsonParserModule = new TSModule("jsonParser", commonsPath, true);
        jsonParser = new TSClass("BvJsonParser", bvJsonParserModule);

    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (method.isConstructor()) {
            TSComplexType methodClass = method.getOwner();
            for (TSField field : implementationSpecificFieldsSet) {
                writer.write("this." + field.getName() + " = " + field.getName() + ";");
                writer.newLine();
            }
        } else {
            RequestMapping methodRequestMapping = method.findAnnotation(RequestMapping.class);
            RequestMapping classRequestMapping = method.getOwner().findAnnotation(RequestMapping.class);

            String tsPath = "this." + FIELD_NAME_URL_SERVICE + ".getBackendUrl() + '" + getPathFromRequestMapping(classRequestMapping) + getPathFromRequestMapping(methodRequestMapping) + "?'";
            String methodString = methodRequestMapping.method()[0].toString();

            writer.write("// path = " + tsPath);
            writer.newLine();
            writer.write("// HTTP method = " + methodString);
            writer.newLine();

            StringBuilder pathStringBuilder = new StringBuilder(tsPath);

            String bodyString = "{}";

            for (TSParameter tsParameter : method.getParameterList()) {

                writer.newLine();

                if (tsParameter.findAnnotation(RequestBody.class) != null) {
                    RequestBody requestBody = tsParameter.findAnnotation(RequestBody.class);
                    writer.write(String.format("// parameter %s is sent in request body ", tsParameter.getName()));

                    bodyString = tsParameter.getName();

                    continue;
                }
                if (tsParameter.findAnnotation(PathVariable.class) != null) {
                    PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
                    writer.write(String.format("// parameter %s is sent in path variable %s ", tsParameter.getName(), pathVariable.value()));

                    String targetToReplace = "{" + pathVariable.value() + "}";
                    int start = pathStringBuilder.lastIndexOf(targetToReplace);
                    int end = start + targetToReplace.length();

                    if ("id".equals(pathVariable.value())) {
                        if (methodString.startsWith("PUT")) {
                            tsPath = tsPath.replace("{id}", "' + entity.id.split('/')[1] + '");
                        } else {
                            pathStringBuilder.replace(start, end, "' + " + tsParameter.getName() + ".split('/')[1] + '");
                        }
                    } else {
                        pathStringBuilder.replace(start, end, "' + " + tsParameter.getName() + " + '");
                    }

                    continue;
                }
                if (tsParameter.findAnnotation(RequestParam.class) != null) {
                    RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
                    writer.write(String.format("// parameter %s is sent as request param %s ", tsParameter.getName(), requestParam.value()));

                    pathStringBuilder.append(" + '");
                    pathStringBuilder.append(requestParam.value());
                    pathStringBuilder.append("=' + ");
                    pathStringBuilder.append(tsParameter.getName());
                    pathStringBuilder.append(" + '&'");

                }
            }

            writer.newLine();

            String requestOptionsVar = "requestOptions";

            boolean isUpdateOperation = "PUT".equals(methodString) || "POST".equals(methodString);

            writer.write("const " + requestOptionsVar + ": RequestOptionsArgs = { method: '"
                    + methodString + "'"
                    + (isUpdateOperation ? ", body: JsonScopedSerializer.stringify( " + bodyString + ", jsonScope )" : "")
                    + getHeaderFromRequestMapping(methodRequestMapping) + "};");
            writer.newLine();

            tsPath = pathStringBuilder.toString();

            if (methodString.compareTo("PUT") == 0) {
                for (TSParameter tsParameter : method.getParameterList()) {
                    if (tsParameter.findAnnotation(RequestBody.class) != null) {
                        tsPath = tsPath.replace("{id}", "' + entity.id.split('/')[1] + '"); //TODO: ugly workaround
                    } else if (tsParameter.findAnnotation(PathVariable.class) != null) {
                        PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
                        if ("id".equals(pathVariable.value())) {
                            tsPath = tsPath.replace("{id}", "' +" + tsParameter.getName() + ".split('/')[1] + '"); //TODO: ugly workaround
                        }
                    }
                }
            }

            TSParameterisedType subjectAnyType = new TSParameterisedType("", subjectClass, TypeMapper.tsAny);
            writer.write("const " + FIELD_NAME_SUBJECT + " = new " + subjectAnyType.getName() + "();");
            writer.newLine();
            writer.write(
                    "this." + FIELD_NAME_HTTP_SERVICE + ".request("
                            + tsPath
                            + ", " + requestOptionsVar + ")"
                            + ".subscribe("
                            + "res => " + FIELD_NAME_SUBJECT + getResponseTypeFromRequestMapping(methodRequestMapping, method.getType())
                            + "(err) => {"
                            + "this." + FIELD_NAME_ERROR_HANDLER_SERVICE + ".handleErrorsIfPresent(err); "
                            + FIELD_NAME_SUBJECT + ".error(err ? err : {});});"
            );
            writer.newLine();

            writer.write("return " + FIELD_NAME_SUBJECT + ".asObservable();");
            writer.newLine();

        }

    }

    private String getPathFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping.path().length > 0) {
            return requestMapping.path()[0];
        }

        if (requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }

        return "";
    }

    private String getHeaderFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping.consumes().length > 0) {
            return ",  headers: new Headers({'Content-type': " + "'" + requestMapping.consumes()[0] + "'})";
        }
        return "";
    }

    private String getResponseTypeFromRequestMapping(RequestMapping requestMapping, TSType methodType) {

        if (methodType == TypeMapper.tsNumber) {
            return ".next(res.text() ? Number(res.text()) : null),";
        }
        if (methodType == TypeMapper.tsBoolean) {
            return ".next(res.text() ? res.text().toLowerCase() === 'true' : false),";
        }
        if (methodType == TypeMapper.tsString) {
            return ".next(res.text() ? res.text() : null),";
        }

        return ".next(res.text() ? new BvJsonParser().parse(res.text()) : null),";
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if (isRestClass(tsMethod.getOwner())) {
            return new TSParameterisedType("", observableClass, tsType);
        }

        return tsType;
    }

    @Override
    public SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType) {
        if (isRestClass(tsComplexType)) {
            SortedSet<TSField> fieldsSet = new TreeSet<>();
            fieldsSet.addAll(implementationSpecificFieldsSet);
            return fieldsSet;
        }
        return Collections.emptySortedSet();
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        if (method.isConstructor() && isRestClass(method.getOwner())) {
            List<TSParameter> tsParameters = new ArrayList<>();
            for (TSField field : implementationSpecificFieldsSet) {
                TSParameter newParameter = new TSParameter(field.getName(), field.getType());
                tsParameters.add(newParameter);
            }
            return tsParameters;
        }
        RequestMapping methodRequestMapping = method.findAnnotation(RequestMapping.class);
        if (methodRequestMapping != null) {
            String methodString = methodRequestMapping.method()[0].toString();
            if ("PUT".equals(methodString) || "POST".equals(methodString)) {
                List<TSParameter> tsParameters = new ArrayList<>();
                TSParameter jsonScopeParameter = new TSParameter("jsonScope", jsonScope, "new JsonScope(false, [])");
                tsParameters.add(jsonScopeParameter);
                return tsParameters;
            }
        }
        return Collections.emptyList();
    }

    private boolean isRestClass(TSComplexType tsComplexType) {
        return tsComplexType.findAnnotation(RequestMapping.class) != null;
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
        if (isRestClass(tsClass)) {
            tsClass.addScopedTypeUsage(observableClass);
            tsClass.addScopedTypeUsage(httpClass);
            tsClass.addScopedTypeUsage(responseClass);
            tsClass.addScopedTypeUsage(requestOptionsClass);
            tsClass.addScopedTypeUsage(headersClass);
            tsClass.addScopedTypeUsage(urlServiceClass);
            tsClass.addScopedTypeUsage(errorHandlerServiceClass);
            tsClass.addScopedTypeUsage(jsonScope);
            tsClass.addScopedTypeUsage(jsonScopedSerializer);
            tsClass.addScopedTypeUsage(subjectClass);
            tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
            tsClass.addScopedTypeUsage(jsonParser);
        }
    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {
        implementationSpecificFieldsSet = new HashSet<>();
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_HTTP_SERVICE, tsComplexType, httpClass));
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_URL_SERVICE, tsComplexType, urlServiceClass));
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_ERROR_HANDLER_SERVICE, tsComplexType, errorHandlerServiceClass));
    }

    @Override
    public void generateImplementationSpecificUtilTypes(GenerationContext generationContext, ModuleConverter moduleConverter) {
        createUrlService(generationContext, moduleConverter);
        createErrorHandlerService(generationContext, moduleConverter);
    }

    private void createUrlService(GenerationContext generationContext, ModuleConverter moduleConverter){
        urlServiceClass.addTsMethod(
                new TSMethod("getBackendUrl",
                        urlServiceClass,
                        TypeMapper.tsString,
                        false,
                        false)
        );
        urlServiceClass.addTsMethod(
                new TSMethod("constructor",
                        urlServiceClass,
                        null,
                        false,
                        true)
        );

        addComplexTypeUsage(urlServiceClass);
        TSModule module = urlServiceClass.getModule();
        module.addScopedType(urlServiceClass);
        generationContext.addImplementationGenerator(urlServiceClass, new UrlServiceImplementationGenerator());
        moduleConverter.getTsModules().add(module);
    }

    private void createErrorHandlerService(GenerationContext generationContext, ModuleConverter moduleConverter){

        TSModule httpModule = new TSModule("@angular/http", null, true);
        TSClass responseClass = new TSClass("Response", httpModule);

        TSClass handlerCondition = new TSClass("HandlerCondition", errorHandlerServiceClass.getModule(), true);
        TSMethod isMet = new TSMethod(
                "isMetForGivenResponse",
                handlerCondition,
                TypeMapper.tsBoolean,
                true,
                false
        );
        isMet.getParameterList().add(new TSParameter("response", responseClass));
        handlerCondition.addTsMethod(isMet);

        TSClass simpleErrorHandlerClass = new TSClass("SimpleErrorHandler", errorHandlerServiceClass.getModule());
        simpleErrorHandlerClass.addTsField(
                new TSField(
                        "condition",
                        simpleErrorHandlerClass,
                        handlerCondition
                )
        );
        simpleErrorHandlerClass.addTsField(
                new TSField(
                        "action",
                        simpleErrorHandlerClass,
                        new TSArrowFuncType(TypeMapper.tsVoid)
                )
        );
        TSMethod simpleErrorHandlerConstructor = new TSMethod(
                "constructor",
                simpleErrorHandlerClass,
                null,
                false,
                true
        );
        simpleErrorHandlerConstructor.getParameterList().add(new TSParameter("condition", handlerCondition));
        simpleErrorHandlerConstructor.getParameterList().add(new TSParameter("action", new TSArrowFuncType(TypeMapper.tsVoid)));
        simpleErrorHandlerClass.addTsMethod(simpleErrorHandlerConstructor);
        TSMethod handleError = new TSMethod(
                "handleErrorIfPresent",
                simpleErrorHandlerClass,
                TypeMapper.tsBoolean,
                false,
                false
        );
        handleError.getParameterList().add(new TSParameter("response", responseClass));
        simpleErrorHandlerClass.addTsMethod(handleError);

        errorHandlerServiceClass.addTsField(
                new TSField(
                        "handlers",
                        errorHandlerServiceClass,
                        new TSArray(simpleErrorHandlerClass)
                )
        );
        errorHandlerServiceClass.addTsMethod(
                new TSMethod(
                        "constructor",
                        errorHandlerServiceClass,
                        null,
                        false,
                        true
                )
        );
        TSMethod handleErrors = new TSMethod(
                "handleErrorsIfPresent",
                simpleErrorHandlerClass,
                TypeMapper.tsBoolean,
                false,
                false
        );
        handleErrors.getParameterList().add(new TSParameter("response", responseClass));
        errorHandlerServiceClass.addTsMethod(handleErrors);
        TSMethod addHandler = new TSMethod(
                "addHandler",
                simpleErrorHandlerClass,
                TypeMapper.tsVoid,
                false,
                false
        );
        addHandler.getParameterList().add(new TSParameter("errorHandlerModule", simpleErrorHandlerClass));
        errorHandlerServiceClass.addTsMethod(addHandler);

        TSModule errorHandlerModule = errorHandlerServiceClass.getModule();
        errorHandlerModule.scopedTypeUsage(responseClass);
        errorHandlerModule.addScopedType(handlerCondition);
        errorHandlerModule.addScopedType(simpleErrorHandlerClass);
        errorHandlerModule.addScopedType(errorHandlerServiceClass);

        addComplexTypeUsage(errorHandlerServiceClass);
        generationContext.addImplementationGenerator(simpleErrorHandlerClass, new ErrorHandlerServiceImplementationGenerator());
        moduleConverter.getTsModules().add(errorHandlerModule);
    }
}
