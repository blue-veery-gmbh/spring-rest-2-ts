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
    private TSClass httpParamsClass;
    private TSClass httpHeadersClass;
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

        TSModule angularHttpModule = new TSModule("@angular/common/http", null, true);
        httpClass = new TSClass("HttpClient", angularHttpModule);
        httpParamsClass = new TSClass("HttpParams", angularHttpModule);
        httpHeadersClass = new TSClass("HttpHeaders", angularHttpModule);

        TSModule urlServiceModule = new TSModule("url.service", sharedPath, false);
        urlServiceClass = new TSClass("UrlService", urlServiceModule);
    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (method.isConstructor()) {
            for (TSField field : implementationSpecificFieldsSet) {
                writer.write("this." + field.getName() + " = " + field.getName() + ";");
                writer.newLine();
            }
        } else {
            RequestMapping methodRequestMapping = method.findAnnotation(RequestMapping.class);
            RequestMapping classRequestMapping = method.getOwner().findAnnotation(RequestMapping.class);

            String tsPath = "this." + FIELD_NAME_URL_SERVICE + ".getBackendUrl() + '" + getPathFromRequestMapping(classRequestMapping) + getPathFromRequestMapping(methodRequestMapping) + "'";
            String httpMethod = methodRequestMapping.method()[0].toString();

            writer.write("// path = " + tsPath);
            writer.newLine();
            writer.write("// HTTP method = " + httpMethod);
            writer.newLine();

            StringBuilder pathStringBuilder = new StringBuilder(tsPath);

            String requestBodyVar = "body";
            String requestHeadersVar = "headers";
            String requestParamsVar = "params";

            String requestBody = "";

            StringBuilder requestParamsBuilder = null;
            for (TSParameter tsParameter : method.getParameterList()) {

                writer.newLine();

                String tsParameterName = tsParameter.getName();
                if (tsParameter.findAnnotation(RequestBody.class) != null) {
                    writer.write(String.format("// parameter %s is sent in request body ", tsParameterName));
                    requestBody = tsParameterName;
                    continue;
                }
                if (tsParameter.findAnnotation(PathVariable.class) != null) {
                    PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
                    writer.write(String.format("// parameter %s is sent in path variable %s ", tsParameterName, pathVariable.value()));

                    String targetToReplace = "{" + pathVariable.value() + "}";
                    int start = pathStringBuilder.lastIndexOf(targetToReplace);
                    int end = start + targetToReplace.length();

                    if ("id".equals(pathVariable.value())) {
                        if (httpMethod.startsWith("PUT")) {
                            tsPath = tsPath.replace("{id}", "' + entity.id + '");
                        } else {
                            pathStringBuilder.replace(start, end, "' + " + tsParameterName + " + '");
                        }
                    } else {
                        pathStringBuilder.replace(start, end, "' + " + tsParameterName + " + '");
                    }

                    continue;
                }
                if (tsParameter.findAnnotation(RequestParam.class) != null) {
                    RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
                    writer.write(String.format("// parameter %s is sent as request param %s ", tsParameterName, requestParam.value()));
                    if (requestParamsBuilder == null) {
                        requestParamsBuilder = new StringBuilder("const " + requestParamsVar + " = new HttpParams();");
                    }
                    if (!tsParameter.getType().equals(TypeMapper.tsString)) {
                        tsParameterName = tsParameterName + ".toString()";
                    }
                    requestParamsBuilder.append("\n").append(requestParamsVar).append(".set('").append(requestParam.value()).append("',").append(tsParameterName).append(");");
                }
            }
            String requestParams = requestParamsBuilder != null ? requestParamsBuilder.toString() : "";
            writer.newLine();

            boolean isRequestBodyDefined = !requestBody.isEmpty();
            if (isRequestBodyDefined) {
                writer.write("const " + requestBodyVar + " = " + requestBody + ";");
                writer.newLine();
            }

            boolean isRequestParamDefined = !requestParams.isEmpty();
            if (isRequestParamDefined) {
                writer.write(requestParams);
                writer.newLine();
            }

            String consumeHeader = getConsumeHeaderFromRequestMapping(methodRequestMapping);
            boolean isRequestHeaderDefined = !consumeHeader.isEmpty();
            if (isRequestHeaderDefined) {
                writer.write("const " + requestHeadersVar + " = " + consumeHeader + ";");
                writer.newLine();
            }

            tsPath = pathStringBuilder.toString();
            if (httpMethod.compareTo("PUT") == 0) {
                for (TSParameter tsParameter : method.getParameterList()) {
                    if (tsParameter.findAnnotation(RequestBody.class) != null) {
                        tsPath = tsPath.replace("{id}", "' + " + tsParameter.getName() + ".id + '"); //TODO: ugly workaround
                    } else if (tsParameter.findAnnotation(PathVariable.class) != null) {
                        PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
                        if ("id".equals(pathVariable.value())) {
                            tsPath = tsPath.replace("{id}", "' +" + tsParameter.getName() + " + '"); //TODO: ugly workaround
                        }
                    }
                }
            }


            TSParameterisedType subjectAnyType = new TSParameterisedType("", subjectClass, TypeMapper.tsAny);
            writer.write("const " + FIELD_NAME_SUBJECT + " = new " + subjectAnyType.getName() + "();");
            writer.newLine();

            String requestOptions = "";
            boolean isMethodProduceApplicationJson = Arrays.asList(methodRequestMapping.produces()).contains("application/json");
            requestOptions = composeRequestOptions(requestBodyVar, requestHeadersVar, requestParamsVar, isRequestBodyDefined, isRequestParamDefined, isRequestHeaderDefined, requestOptions, isMethodProduceApplicationJson);

            writer.write(
                    "this." + FIELD_NAME_HTTP_SERVICE + ".request("
                            + "'" + httpMethod + "'"
                            + ", " + tsPath
                            + requestOptions
                            + ").subscribe("
                            + "res => " + FIELD_NAME_SUBJECT + getResponseFromRequestMapping(method.getType())
                            + "(err) => {"
                            + FIELD_NAME_SUBJECT + ".error(err ? err : {});});"
            );
            writer.newLine();

            writer.write("return " + FIELD_NAME_SUBJECT + ".asObservable();");
            writer.newLine();

        }

    }

    private String composeRequestOptions(String requestBodyVar, String requestHeadersVar, String requestParamsVar, boolean isRequestBodyDefined, boolean isRequestParamDefined, boolean isRequestHeaderDefined, String requestOptions, boolean methodProduceApplicationJson) {
        if (isRequestHeaderDefined || isRequestParamDefined || isRequestBodyDefined || !methodProduceApplicationJson) {
            List<String> requestOptionsList = new ArrayList<>();
            if (isRequestHeaderDefined) {
                requestOptionsList.add(requestHeadersVar);
            }
            if (isRequestParamDefined) {
                requestOptionsList.add(requestParamsVar);
            }
            if (isRequestBodyDefined) {
                requestOptionsList.add(requestBodyVar);
            }
            if (!methodProduceApplicationJson) {
                requestOptionsList.add("responseType: 'text'");
            }

            requestOptions += ", {";
            requestOptions += String.join(", ", requestOptionsList);
            requestOptions += "}";
        }
        return requestOptions;
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

    private String getConsumeHeaderFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping.consumes().length > 0) {
            return " new HttpHeaders().set('Content-type'," + " '" + requestMapping.consumes()[0] + "')";
        }
        return "";
    }

    private String getResponseFromRequestMapping(TSType methodType) {
        if (methodType == TypeMapper.tsNumber) {
            return ".next(res ? Number(res) : null),";
        }
        if (methodType == TypeMapper.tsBoolean) {
            return ".next(res ? res.toLowerCase() === 'true' : false),";
        }

        return ".next(res ?  res : null),";
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
            tsClass.addScopedTypeUsage(httpParamsClass);
            tsClass.addScopedTypeUsage(httpHeadersClass);
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
