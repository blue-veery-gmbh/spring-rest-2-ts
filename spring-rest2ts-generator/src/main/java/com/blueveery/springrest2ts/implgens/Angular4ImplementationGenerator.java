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
import java.util.*;

public class Angular4ImplementationGenerator implements ImplementationGenerator {
    private TSDecorator injectableDecorator;
    private TSClass observableClass;
    private TSClass httpClass;
    private TSClass responseClass;
    private TSClass requestOptionsClass;
    private TSClass headersClass;
    private TSClass urlServiceClass;
    private TSClass subjectClass;

    private Set<TSField> implementationSpecificFieldsSet;

    private final String FIELD_NAME_HTTP_SERVICE = "httpService";
    private final String FIELD_NAME_URL_SERVICE = "urlService";
    private final String FIELD_NAME_SUBJECT = "subject";

    public Angular4ImplementationGenerator(Path commonsPath, Path sharedPath) {
        TSModule angularCoreModule = new TSModule("@angular/core", null, true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));

        TSModule rxjsModule = new TSModule("rxjs", null, true);
        observableClass = new TSClass("Observable", rxjsModule);
        subjectClass = new TSClass("Subject", rxjsModule);

        TSModule angularHttpModule = new TSModule("@angular/http", null, true);
        httpClass = new TSClass("Http", angularHttpModule);
        responseClass = new TSClass("Response", angularHttpModule);
        requestOptionsClass = new TSClass("RequestOptionsArgs", angularHttpModule);
        headersClass = new TSClass("Headers", angularHttpModule);

        TSModule urlServiceModule = new TSModule("url.service", sharedPath, false);
        urlServiceClass = new TSClass("UrlService", urlServiceModule);
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
                    + (isUpdateOperation ? ", body: JSON.stringify( " + bodyString + " )" : "")
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

        return ".next(res.text() ?  JSON.parse(res.text()) : null),";
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
            tsClass.addScopedTypeUsage(subjectClass);
            tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
        }
    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {
        implementationSpecificFieldsSet = new HashSet<>();
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_HTTP_SERVICE, tsComplexType, httpClass));
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_URL_SERVICE, tsComplexType, urlServiceClass));
    }

    @Override
    public void generateImplementationSpecificUtilTypes(GenerationContext generationContext, ModuleConverter moduleConverter) {
        createUrlService(generationContext, moduleConverter);
    }

    private void createUrlService(GenerationContext generationContext, ModuleConverter moduleConverter) {
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
}
