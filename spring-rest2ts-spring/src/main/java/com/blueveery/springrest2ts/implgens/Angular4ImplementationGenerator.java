package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
    private Set<TSField> implementationSpecificFieldsSet;

    private boolean useUrlService;

    public Angular4ImplementationGenerator() {
        this(null);
    }

    public Angular4ImplementationGenerator(Path urlServicePath) {
        TSModule angularCoreModule = new TSModule("@angular/core", null, true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));

        TSModule observableModule = new TSModule("rxjs/Observable", null, true);
        observableClass = new TSClass("Observable", observableModule, this);

        TSModule angularHttpModule = new TSModule("@angular/common/http", null, true);
        httpClass = new TSClass("HttpClient", angularHttpModule, this);
        httpParamsClass = new TSClass("HttpParams", angularHttpModule, this);
        httpHeadersClass = new TSClass("HttpHeaders", angularHttpModule, this);

        useUrlService = urlServicePath != null;
        if (useUrlService) {
            TSModule urlServiceModule = new TSModule("url.service", urlServicePath, false);
            urlServiceClass = new TSClass("UrlService", urlServiceModule, this);
        }
    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (method.isConstructor()) {
            for (TSField field : implementationSpecificFieldsSet) {
                writer.write("this." + field.getName() + " = " + field.getName() + ";");
            }
        } else {
            RequestMapping methodRequestMapping = getRequestMapping(method.getAnnotationList());
            RequestMapping classRequestMapping = getRequestMapping(method.getOwner().getAnnotationList());

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
            writeRequestOption(writer, requestParamsVar, requestParamsBuilder.toString(), isRequestParamDefined);

            String consumeHeader = getConsumeContentTypeFromRequestMapping(methodRequestMapping);
            boolean isRequestHeaderDefined = !consumeHeader.isEmpty();
            writeRequestOption(writer, requestHeadersVar, consumeHeader, isRequestHeaderDefined);

            String requestOptions = "";
            boolean isMethodProduceTextContent = Arrays.asList(methodRequestMapping.produces()).contains("text/plain");
            requestOptions = composeRequestOptions(requestBodyBuilder.toString(), requestHeadersVar, requestParamsVar, isRequestBodyDefined, isRequestParamDefined, isRequestHeaderDefined, requestOptions, isMethodProduceTextContent);

            tsPath = pathStringBuilder.toString();
            writer.write(
                    "return this." + FIELD_NAME_HTTP_SERVICE + "." + httpMethod.toLowerCase() + "<" + method.getType().getName() + ">" + "("
                            + tsPath
                            + requestOptions
                            + ");");

        }
    }

    protected void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, TSParameter tsParameter, String requestParamName) {
        String tsParameterName = callToStringOnPArameterIfRequired(tsParameter);
        requestParamsBuilder
                .append("\n")
                .append(requestParamsVar)
                .append(" = ")
                .append(requestParamsVar)
                .append(".set('")
                .append(requestParamName)
                .append("',").append(tsParameterName)
                .append(");");
    }


    protected void initializeHttpParams(StringBuilder requestParamsBuilder) {
        if (isStringBuilderEmpty(requestParamsBuilder)) {
            requestParamsBuilder.append(" new HttpParams();");
        }
    }

    private void writeRequestOption(BufferedWriter writer, String requestOption, String requestOptionValue, boolean isOptionDefined) throws IOException {
        if (isOptionDefined) {
            writer.write("let " + requestOption + " = " + requestOptionValue);
            writer.newLine();
        }
    }

    private String composeRequestOptions(String requestBody, String requestHeadersVar, String requestParamsVar, boolean isRequestBodyDefined, boolean isRequestParamDefined, boolean isRequestHeaderDefined, String requestOptions, boolean isMethodProduceTextContent) {
        if (isRequestBodyDefined) {
            requestOptions += ", " + requestBody + " ";
        }
        if (isRequestHeaderDefined || isRequestParamDefined || isMethodProduceTextContent) {
            List<String> requestOptionsList = new ArrayList<>();
            if (isRequestHeaderDefined) {
                requestOptionsList.add(requestHeadersVar);
            }
            if (isRequestParamDefined) {
                requestOptionsList.add(requestParamsVar);
            }
            if (isMethodProduceTextContent) {
                requestOptionsList.add("responseType: 'text'");
            }

            requestOptions += ", {";
            requestOptions += String.join(", ", requestOptionsList);
            requestOptions += "}";
        }
        return requestOptions;
    }


    private String getConsumeContentTypeFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping.consumes().length > 0) {
            return " new HttpHeaders().set('Content-type'," + " '" + requestMapping.consumes()[0] + "');";
        }
        return "";
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
                TSParameter newParameter = new TSParameter(field.getName(), field.getType(), this);
                tsParameters.add(newParameter);
            }
            return tsParameters;
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
            tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
            if (useUrlService) {
                tsClass.addScopedTypeUsage(urlServiceClass);
            }
        }
    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {
        implementationSpecificFieldsSet = new HashSet<>();
        implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_HTTP_SERVICE, tsComplexType, httpClass));
        if (useUrlService) {
            implementationSpecificFieldsSet.add(new TSField(FIELD_NAME_URL_SERVICE, tsComplexType, urlServiceClass));
        }
    }
}
