package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blueveery.springrest2ts.spring.RequestMappingUtility.getRequestMapping;

public class Angular4ImplementationGenerator extends BaseImplementationGenerator {
    private static final String FIELD_NAME_HTTP_SERVICE = "httpService";
    private static final String FIELD_NAME_URL_SERVICE = "urlService";

    private TSDecorator injectableDecorator;
    private TSClass observableClass;
    private TSClass httpClass;
    private TSClass httpParamsClass;
    private TSClass httpHeadersClass;
    private TSClass urlServiceClass;
    private String[] implementationSpecificFieldNames;

    private boolean useUrlService;

    public Angular4ImplementationGenerator() {
        this(null);
    }

    public Angular4ImplementationGenerator(Path urlServicePath) {
        TSModule angularCoreModule = new TSModule("@angular/core", null, true);
        injectableDecorator = new TSDecorator(new TSFunction("Injectable", angularCoreModule));

        TSModule observableModule = new TSModule("rxjs", null, true);
        observableClass = new TSClass("Observable", observableModule, this);

        TSModule angularHttpModule = new TSModule("@angular/common/http", null, true);
        httpClass = new TSClass("HttpClient", angularHttpModule, this);
        httpParamsClass = new TSClass("HttpParams", angularHttpModule, this);
        httpHeadersClass = new TSClass("HttpHeaders", angularHttpModule, this);

        useUrlService = urlServicePath != null;
        if (useUrlService) {
            implementationSpecificFieldNames = new String[]{FIELD_NAME_HTTP_SERVICE, FIELD_NAME_URL_SERVICE};
            TSModule urlServiceModule = new TSModule("url.service", urlServicePath, false);
            urlServiceClass = new TSClass("UrlService", urlServiceModule, this);
        } else {
            implementationSpecificFieldNames = new String[]{FIELD_NAME_HTTP_SERVICE};
        }
    }

    @Override
    protected String[] getImplementationSpecificFieldNames() {
        return implementationSpecificFieldNames;
    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        TSClass tsClass = (TSClass) method.getOwner();
        if (method.isConstructor()) {
            writeConstructorImplementation(writer, tsClass);
        } else {
            RequestMapping methodRequestMapping = getRequestMapping(method.getAnnotationList());
            RequestMapping classRequestMapping = getRequestMapping(tsClass.getAnnotationList());

            String tsPath = useUrlService ? "this." + FIELD_NAME_URL_SERVICE + ".getBackendUrl() + '" : "'";
            tsPath += getPathFromRequestMapping(classRequestMapping) + getPathFromRequestMapping(methodRequestMapping) + "'";
            String httpMethod = methodRequestMapping.method()[0].toString();

            String requestHeadersVar = "headers";
            String requestParamsVar = "params";

            StringBuilder pathStringBuilder = new StringBuilder(tsPath);
            StringBuilder requestBodyBuilder = new StringBuilder();
            StringBuilder requestParamsBuilder = new StringBuilder();

            assignMethodParameters(method, requestParamsVar, pathStringBuilder, requestBodyBuilder, requestParamsBuilder);

            boolean isRequestBodyDefined = !isStringBuilderEmpty(requestBodyBuilder);
            boolean isRequestParamDefined = !isStringBuilderEmpty(requestParamsBuilder);
            writer.write(requestParamsBuilder.toString());
            writer.newLine();

            String contentTypeHeader = getContentTypeHeaderFromRequestMapping(httpMethod, methodRequestMapping, isRequestBodyDefined);
            boolean isRequestHeaderDefined = !contentTypeHeader.isEmpty();
            writeRequestOption(writer, requestHeadersVar, contentTypeHeader, isRequestHeaderDefined);

            String requestOptions = "";
            requestOptions = composeRequestOptions(requestBodyBuilder.toString(), requestHeadersVar, requestParamsVar, isRequestBodyDefined, isRequestParamDefined, isRequestHeaderDefined, requestOptions, httpMethod);

            tsPath = pathStringBuilder.toString();
            writer.write(
                    "return this." + FIELD_NAME_HTTP_SERVICE + "." + httpMethod.toLowerCase() + "<" + method.getType().getName() + ">" + "("
                            + tsPath
                            + requestOptions
                            + ");");

        }
    }


    protected void initializeHttpParams(StringBuilder requestParamsBuilder, String requestParamsVar) {
        requestParamsBuilder
                .append("let ")
                .append(requestParamsVar)
                .append(" = new HttpParams();");
    }

    @Override
    protected void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, String queryParamVar) {
        requestParamsBuilder
                .append("\n")
                .append(requestParamsVar)
                .append(" = ")
                .append(requestParamsVar)
                .append(".append(").append(queryParamVar).append(".name")
                .append(",").append(queryParamVar).append(".value")
                .append(");");
    }

    private void writeRequestOption(BufferedWriter writer, String requestOption, String requestOptionValue, boolean isOptionDefined) throws IOException {
        if (isOptionDefined) {
            writer.write("let " + requestOption + " = " + requestOptionValue);
            writer.newLine();
        }
    }

    private String composeRequestOptions(String requestBody, String requestHeadersVar, String requestParamsVar, boolean isRequestBodyDefined, boolean isRequestParamDefined, boolean isRequestHeaderDefined, String requestOptions, String httpMethod) {
        if (isPutOrPostMethod(httpMethod)) {
            if (isRequestBodyDefined) {
                requestOptions += ", " + requestBody + " ";
            } else {
                requestOptions += ", null ";
            }
        }
        if (isRequestHeaderDefined || isRequestParamDefined) {
            List<String> requestOptionsList = new ArrayList<>();
            if (isRequestHeaderDefined) {
                requestOptionsList.add(requestHeadersVar);
            }
            if (isRequestParamDefined) {
                requestOptionsList.add(requestParamsVar);
            }

            requestOptions += ", {";
            requestOptions += String.join(", ", requestOptionsList);
            requestOptions += "}";
        }
        return requestOptions;
    }


    private String getContentTypeHeaderFromRequestMapping(String httpMethod, RequestMapping requestMapping, boolean isRequestBodyDefined) {
        if (isPutOrPostMethod(httpMethod) && isRequestBodyDefined) {
            String contentType = getConsumesContentType(requestMapping.consumes());
            return " new HttpHeaders().set('Content-type'," + " '" + contentType + "');";
        }
        return "";
    }

    private boolean isPutOrPostMethod(String httpMethod) {
        return "PUT".equals(httpMethod) || "POST".equals(httpMethod);
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if (isRestClass(tsMethod.getOwner())) {
            return new TSParameterisedType("", observableClass, tsType);
        }
        return tsType;
    }


    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        if (method.isConstructor()) {
            List<TSParameter> tsParameters = new ArrayList<>();
            TSParameter httpServiceParameter = new TSParameter(FIELD_NAME_HTTP_SERVICE, httpClass, method, this);
            tsParameters.add(httpServiceParameter);
            if (useUrlService) {
                TSParameter urlServiceParameter = new TSParameter(FIELD_NAME_URL_SERVICE, urlServiceClass, method, this);
                tsParameters.add(urlServiceParameter);
            }
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
        tsClass.addScopedTypeUsage(observableClass);
        tsClass.addScopedTypeUsage(httpClass);
        tsClass.addScopedTypeUsage(httpParamsClass);
        tsClass.addScopedTypeUsage(httpHeadersClass);
        tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
        if (useUrlService) {
            tsClass.addScopedTypeUsage(urlServiceClass);
        }
    }

    @Override
    public void addImplementationSpecificFields(TSComplexElement tsComplexType) {
        TSClass tsClass = (TSClass) tsComplexType;
        if (tsClass.getExtendsClass() == null) {
            tsClass.getTsFields().add(new TSField(FIELD_NAME_HTTP_SERVICE, tsComplexType, httpClass));
            if (useUrlService) {
                tsClass.getTsFields().add(new TSField(FIELD_NAME_URL_SERVICE, tsComplexType, urlServiceClass));
            }
        }
    }
}
