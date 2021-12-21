package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.blueveery.springrest2ts.spring.RequestMappingUtility.getRequestMapping;

public class Angular4ImplementationGenerator extends BaseImplementationGenerator {
    protected static final String FIELD_NAME_HTTP_SERVICE = "httpService";
    protected static final String FIELD_NAME_URL_SERVICE = "urlService";

    protected TSDecorator injectableDecorator;
    protected TSClass observableClass;
    protected TSClass httpClass;
    protected TSClass httpParamsClass;
    protected TSClass httpHeadersClass;
    protected TSClass urlServiceClass;
    protected TSClass mapOperatorClass;
    protected String[] implementationSpecificFieldNames;

    protected boolean useUrlService;

    public Angular4ImplementationGenerator() {
        this(null);
    }

    public Angular4ImplementationGenerator(Path urlServicePath) {
        TSModule angularCoreModule = new TSModule("@angular/core", null, true);
        injectableDecorator = new TSDecorator(new TSFunction("Injectable", angularCoreModule));

        TSModule observableModule = new TSModule("rxjs", null, true);
        observableClass = new TSClass("Observable", observableModule, this);

        TSModule rxjsOperatorsModule = new TSModule("rxjs/operators", null, true);
        mapOperatorClass = new TSClass("map", rxjsOperatorsModule, this);

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

            String tsPath = getEndpointPath(methodRequestMapping, classRequestMapping);
            tsPath = useUrlService ? "this." + FIELD_NAME_URL_SERVICE + ".getBackendUrl() + '" + tsPath : "'" + tsPath;

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

            boolean isJsonParsingRequired = isJsonTransformationRequired(method.getType());
            String contentTypeHeader = getContentTypeHeaderFromRequestMapping(httpMethod, methodRequestMapping, isRequestBodyDefined);
            boolean isRequestHeaderDefined = !contentTypeHeader.isEmpty();
            writeRequestOption(writer, requestHeadersVar, contentTypeHeader, isRequestHeaderDefined);

            String requestOptions = "";
            String requestBody = requestBodyBuilder.toString();

            List<TSParameter> requestBodyParam = findRequestBodyParam(method);
            boolean isJsonSerializationRequired = !requestBodyParam.isEmpty() && isJsonTransformationRequired(requestBodyParam.get(0).getType());
            requestOptions = composeRequestBody(
                    requestBody, isRequestBodyDefined, requestOptions, httpMethod, isJsonSerializationRequired,
                    methodRequestMapping.consumes(), method.getType()
            );
            requestOptions = composeRequestOptions(requestHeadersVar, requestParamsVar, isRequestParamDefined, isRequestHeaderDefined, requestOptions, isJsonParsingRequired);

            tsPath = pathStringBuilder.toString();
            writeReturnStatement(writer, httpMethod.toLowerCase(), method, tsPath, requestOptions, isJsonParsingRequired);
        }
    }

    private List<TSParameter> findRequestBodyParam(TSMethod method) {
        return method.getParameterList()
                .stream()
                .filter(
                        p -> p.getAnnotationList()
                                .stream()
                                .anyMatch(a -> a instanceof RequestBody)
                )
                .collect(Collectors.toList());
    }

    protected void writeReturnStatement(BufferedWriter writer, String httpMethod, TSMethod method,
                                        String tsPath, String requestOptions, boolean isJsonParsingRequired) throws IOException {
        writer.write("    return this." + FIELD_NAME_HTTP_SERVICE + "." + httpMethod + getGenericType(method, isJsonParsingRequired) + "("
                + tsPath
                + requestOptions
                + ")" + getParseResponseFunction(isJsonParsingRequired, method.getType()) + ";");
    }

    protected String getParseResponseFunction(boolean isJsonResponse, TSType returnType) {
        if (isJsonResponse) {
            String parseFunction = modelSerializerExtension.generateDeserializationCode("res", returnType);
            return ".pipe(map(res => " + parseFunction + "))";
        }
        return "";
    }

    protected String getGenericType(TSMethod method, boolean isRequestOptionRequired) {
        return isRequestOptionRequired ? "" : "<" + method.getType().getName() + ">";
    }


    protected void initializeHttpParams(StringBuilder requestParamsBuilder, String requestParamsVar) {
        requestParamsBuilder
                .append("    let ")
                .append(requestParamsVar)
                .append(" = new HttpParams();");
    }

    @Override
    protected void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, String queryParamVar) {
        requestParamsBuilder
                .append("\n      ")
                .append(requestParamsVar)
                .append(" = ")
                .append(requestParamsVar)
                .append(".append(").append(queryParamVar).append(".name")
                .append(", ").append(queryParamVar).append(".value")
                .append(");");
    }

    protected boolean isJsonTransformationRequired(TSType type) {
        return type != TypeMapper.tsNumber && type != TypeMapper.tsBoolean && type != TypeMapper.tsString && type != TypeMapper.tsVoid;
    }

    protected void writeRequestOption(
            BufferedWriter writer, String requestOption, String requestOptionValue, boolean isOptionDefined
    ) throws IOException {
        if (isOptionDefined) {
            writer.write("    const " + requestOption + " = " + requestOptionValue);
            writer.newLine();
        }
    }

    protected String composeRequestBody(
            String requestBody, boolean isRequestBodyDefined, String requestOptions, String httpMethod,
            boolean isJsonParsingRequired, String[] consumes, TSType returnType
    ) {
        if (isPutOrPostMethod(httpMethod)) {
            if (isRequestBodyDefined) {
                requestOptions = appendRequestBodyPart(requestBody, requestOptions, isJsonParsingRequired, consumes, returnType);
            } else {
                requestOptions += ", null ";
            }
        }
        return requestOptions;
    }

    protected String appendRequestBodyPart(
            String requestBody, String requestOptions, boolean isJsonParsingRequired, String[] consumes, TSType returnType
    ) {
        if (isJsonParsingRequired) {
            requestOptions += ", " + modelSerializerExtension.generateSerializationCode(requestBody, returnType) + " ";
        } else {
            requestOptions += ", " + requestBody + " ";
        }
        return requestOptions;
    }

    protected String composeRequestOptions(
            String requestHeadersVar, String requestParamsVar, boolean isRequestParamDefined,
            boolean isRequestHeaderDefined, String requestOptions, boolean isJsonParsingRequired
    ) {
        if (isRequestHeaderDefined || isRequestParamDefined || isJsonParsingRequired) {
            List<String> requestOptionsList = new ArrayList<>();
            if (isRequestHeaderDefined) {
                requestOptionsList.add(requestHeadersVar);
            }
            if (isRequestParamDefined) {
                requestOptionsList.add(requestParamsVar);
            }
            if (isJsonParsingRequired) {
                requestOptionsList.add("responseType: 'text'");
            }
            requestOptions += ", {";
            requestOptions += String.join(", ", requestOptionsList);
            requestOptions += "}";
        }
        return requestOptions;
    }

    protected String getContentTypeHeaderFromRequestMapping(
            String httpMethod, RequestMapping requestMapping, boolean isRequestBodyDefined
    ) {
        if (isPutOrPostMethod(httpMethod) && isRequestBodyDefined) {
            String contentType = getContentType(requestMapping.consumes());
            return "new HttpHeaders().set('Content-type'," + " '" + contentType + "');";
        }
        return "";
    }

    protected boolean isPutOrPostMethod(String httpMethod) {
        return "PUT".equals(httpMethod) || "POST".equals(httpMethod);
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if (isRestClass(tsMethod.getOwner())) {
            return new TSClassReference(observableClass, tsType);
        }
        return tsType;
    }


    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        List<TSParameter> tsParameters = new ArrayList<>();
        if (method.isConstructor()) {
            TSParameter httpServiceParameter = new TSParameter(FIELD_NAME_HTTP_SERVICE, httpClass, method, this);
            tsParameters.add(httpServiceParameter);
            if (useUrlService) {
                TSParameter urlServiceParameter = new TSParameter(FIELD_NAME_URL_SERVICE, urlServiceClass, method, this);
                tsParameters.add(urlServiceParameter);
            }
        }
        return tsParameters;
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
        tsClass.addScopedTypeUsage(mapOperatorClass);
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
