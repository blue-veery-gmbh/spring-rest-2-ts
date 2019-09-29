package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import static com.blueveery.springrest2ts.spring.RequestMappingUtility.getRequestMapping;

public class FetchBasedImplementationGenerator implements ImplementationGenerator {

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if (!method.isConstructor()) {
            RequestMapping methodRequestMapping = getRequestMapping(method.getAnnotationList());
            RequestMapping classRequestMapping = getRequestMapping(method.getOwner().getAnnotationList());

            String tsPath = getPathFromRequestMapping(classRequestMapping) + getPathFromRequestMapping(methodRequestMapping) + "'";
            String httpMethod = methodRequestMapping.method()[0].toString();

            String requestUrlVar = "url";
            String requestBodyVar = "body";
            String requestParamsVar = "url.searchParams";

            StringBuilder pathStringBuilder = new StringBuilder(tsPath);
            StringBuilder requestBodyBuilder = new StringBuilder();
            StringBuilder requestParamsBuilder = new StringBuilder();

            readMethodParameters(writer, method, httpMethod, requestParamsVar, pathStringBuilder, requestBodyBuilder, requestParamsBuilder);
            tsPath = pathStringBuilder.toString();
            writer.write("const " + requestUrlVar + " = " + " new URL('" + tsPath + ");");
            writer.newLine();

            boolean isRequestBodyDefined = !isStringBuilderEmpty(requestBodyBuilder);
            if (isRequestBodyDefined) {
                requestBodyVar = requestBodyBuilder.toString();
            }

            writer.write(requestParamsBuilder.toString());
            writer.newLine();

            String requestOptions = composeRequestOptions(requestBodyVar, isRequestBodyDefined, httpMethod, methodRequestMapping.consumes());

            writer.write(
                    "return fetch(" + requestUrlVar + ".toString(), {"
                            + "method: '" + httpMethod + (requestOptions.isEmpty() ? "'" : "',")
                            + requestOptions
                            + "}).then(" + getContentFromResponseFunction() + ");");
        }

    }

    private String getContentFromResponseFunction() {
        String methodBase = "res => res";
        return methodBase + ".json()";
    }

    private void readMethodParameters(BufferedWriter writer, TSMethod method, String httpMethod, String requestParamsVar, StringBuilder pathStringBuilder, StringBuilder requestBodyBuilder, StringBuilder requestParamsBuilder) throws IOException {
        for (TSParameter tsParameter : method.getParameterList()) {
            String tsParameterName = tsParameter.getName();

            if (tsParameter.findAnnotation(RequestBody.class) != null) {
                requestBodyBuilder.append(tsParameterName);
                continue;
            }
            PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String variableName = pathVariable.value();
                if ("".equals(variableName)) {
                    variableName = tsParameterName;
                }

                String targetToReplace = "{" + variableName + "}";
                replaceInStringBuilder(pathStringBuilder, targetToReplace, "' + " + tsParameterName + " + '");

                continue;
            }
            RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
            if (requestParam != null) {
                String requestParamName = requestParam.value();
                if ("".equals(requestParamName)) {
                    requestParamName = tsParameter.getName();
                }

                boolean isNullableType = tsParameter.isNullable();
                if (tsParameter.isOptional() || isNullableType) {
                    requestParamsBuilder
                            .append("\n")
                            .append("if (")
                            .append(tsParameterName)
                            .append(" !== undefined && ")
                            .append(tsParameterName)
                            .append(" !== null) {");
                    addRequestParameter(requestParamsBuilder, requestParamsVar, tsParameter, requestParamName);
                    requestParamsBuilder.append("}");
                } else {
                    addRequestParameter(requestParamsBuilder, requestParamsVar, tsParameter, requestParamName);
                }
            }

        }
    }

    private void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, TSParameter tsParameter, String requestParamName) {
        String tsParameterName = tsParameter.getName();
        if (!tsParameter.getType().equals(TypeMapper.tsString)) {
            tsParameterName += ".toString()";
        }
        requestParamsBuilder
                .append("\n")
                .append(requestParamsVar)
                .append(".append('")
                .append(requestParamName)
                .append("',").append(tsParameterName)
                .append(");");
    }

    private boolean isStringBuilderEmpty(StringBuilder requestParamsBuilder) {
        return requestParamsBuilder.length() == 0;
    }

    private void replaceInStringBuilder(StringBuilder pathStringBuilder, String targetToReplace, String replacement) {
        int start = pathStringBuilder.lastIndexOf(targetToReplace);
        int end = start + targetToReplace.length();
        pathStringBuilder.replace(start, end, replacement);
    }

    private String composeRequestOptions(String requestBodyVar, boolean isRequestBodyDefined, String httpMethod, String[] consumesContentType) {
        String requestOptions = "";
        List<String> requestOptionsList = new ArrayList<>();
        if ("PUT".equals(httpMethod) || "POST".equals(httpMethod)) {
            String headers = "headers: {";
            headers += "'Content-Type': '" + consumesContentType[0] + "'";
            headers += "}";
            requestOptionsList.add(headers);
        }
        if (isRequestBodyDefined) {
            requestOptionsList.add("body: JSON.stringify(" + requestBodyVar + ")");
        }

        requestOptions += String.join(", ", requestOptionsList);
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

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if (isRestClass(tsMethod.getOwner())) {
            return new TSParameterisedType("", new TSInterface("Promise", null), tsType);
        }
        return tsType;
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
        return Collections.emptyList();
    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {

    }

    @Override
    public void addImplementationSpecificFields(TSComplexType tsComplexType) {

    }

    private boolean isRestClass(TSComplexType tsComplexType) {
        return tsComplexType.findAnnotation(RequestMapping.class) != null;
    }

}
