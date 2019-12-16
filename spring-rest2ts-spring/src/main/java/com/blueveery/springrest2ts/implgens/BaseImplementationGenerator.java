package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.extensions.ConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public abstract class BaseImplementationGenerator implements ImplementationGenerator {

    protected List<? extends ConversionExtension> extensionSet;

    @Override
    public void setExtensions(List<? extends ConversionExtension> conversionExtensionSet) {
        this.extensionSet = conversionExtensionSet;
    }

    protected abstract void initializeHttpParams(StringBuilder requestParamsBuilder, String requestParamsVar);

    protected void writeConstructorImplementation(BufferedWriter writer, TSClass tsClass) throws IOException {

        if (tsClass.getExtendsClass() == null) {
            for (String name : getImplementationSpecificFieldNames()) {
                writer.write("this." + name + " = " + name + ";");
            }
        } else {
            writer.write("super(");
            writer.write(String.join(",", getImplementationSpecificFieldNames()));
            writer.write(");");
        }
    }

    protected abstract void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, String queryParamVar);

    protected String getPathFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping.path().length > 0) {
            return requestMapping.path()[0];
        }
        if (requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        return "";
    }

    protected void replaceInStringBuilder(StringBuilder pathStringBuilder, String targetToReplace, String replacement) {
        int start = pathStringBuilder.lastIndexOf(targetToReplace);
        validatePathVariableParameter(pathStringBuilder, targetToReplace, start);
        int end = start + targetToReplace.length();
        pathStringBuilder.replace(start, end, replacement);
    }

    private void validatePathVariableParameter(StringBuilder pathStringBuilder, String targetToReplace, int start) {
        if (start == -1) {
            throw new IllegalStateException(String.format("Cannot find argument: %s in path %s. Add name in PathVariable annotation or configure compiler option to not optimize parameters", targetToReplace, pathStringBuilder));
        }
    }

    protected String callToStringOnParameterIfRequired(TSParameter tsParameter) {
        String tsParameterName = tsParameter.getName();
        if (!tsParameter.getType().equals(TypeMapper.tsString)) {
            tsParameterName += ".toString()";
        }
        return tsParameterName;
    }


    protected void assignMethodParameters(TSMethod method, String requestParamsVar, StringBuilder pathStringBuilder, StringBuilder requestBodyBuilder, StringBuilder requestParamsBuilder) {
        StringBuilder queryParamsListBuilder = new StringBuilder();
        String queryParamsListVar = "queryParamsList";

        for (TSParameter tsParameter : method.getParameterList()) {
            String tsParameterName = tsParameter.getName();

            if (tsParameter.findAnnotation(RequestBody.class) != null) {
                requestBodyBuilder.append(tsParameterName);
                continue;
            }
            PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
            if (pathVariable != null) {
                addPathVariable(pathStringBuilder, tsParameterName, pathVariable);
                continue;
            }
            RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
            if (requestParam != null) {
                String requestParamName = getRequestParamName(tsParameter, requestParam);
                boolean isNullableType = tsParameter.isNullable();
                if (tsParameter.isOptional() || isNullableType) {
                    queryParamsListBuilder
                            .append("\n")
                            .append("if (")
                            .append(tsParameterName)
                            .append(" !== undefined && ")
                            .append(tsParameterName)
                            .append(" !== null) {");
                    queryParamsListBuilder.append(String.format("%s.push({name: '%s', value: %s});", queryParamsListVar, requestParamName, callToStringOnParameterIfRequired(tsParameter)));
                    queryParamsListBuilder.append("}");
                } else {
                    queryParamsListBuilder.append(String.format("%s.push({name: '%s', value: %s});", queryParamsListVar, requestParamName, callToStringOnParameterIfRequired(tsParameter)));
                }
            }
            for (ConversionExtension conversionExtension : extensionSet) {
                RestConversionExtension restConversionExtension = (RestConversionExtension) conversionExtension;
                if (restConversionExtension.isMappedRestParam(tsParameter)) {
                    queryParamsListBuilder.append(restConversionExtension.generateImplementation(tsParameter, "pathParamsList", queryParamsListVar, "headerParamsList"));
                }
            }
        }
        if (!isStringBuilderEmpty(queryParamsListBuilder)) {
            fillUpRequestParamsBuilder(requestParamsVar, requestParamsBuilder, queryParamsListBuilder, queryParamsListVar);
        }
    }

    private void fillUpRequestParamsBuilder(String requestParamsVar, StringBuilder requestParamsBuilder, StringBuilder queryParamsListBuilder, String queryParamsListVar) {
        queryParamsListBuilder.insert(0, "const " + queryParamsListVar + " : { name: string, value: string }[] = [];");
        requestParamsBuilder.append(queryParamsListBuilder);
        initializeHttpParams(requestParamsBuilder, requestParamsVar);
        String queryParamVar = "queryParam";
        requestParamsBuilder.append(String.format("for(const %s of %s) {", queryParamVar, queryParamsListVar));
        addRequestParameter(requestParamsBuilder, requestParamsVar, queryParamVar);
        requestParamsBuilder.append("}");
    }

    private String getRequestParamName(TSParameter tsParameter, RequestParam requestParam) {
        String requestParamName = requestParam.value();
        if ("".equals(requestParamName)) {
            requestParamName = tsParameter.getName();
        }
        return requestParamName;
    }

    protected void addPathVariable(StringBuilder pathStringBuilder, String tsParameterName, PathVariable pathVariable) {
        String variableName = pathVariable.value();
        if ("".equals(variableName)) {
            variableName = tsParameterName;
        }

        String targetToReplace = "{" + variableName + "}";
        replaceInStringBuilder(pathStringBuilder, targetToReplace, "' + " + tsParameterName + " + '");
    }

    protected boolean isStringBuilderEmpty(StringBuilder requestParamsBuilder) {
        return requestParamsBuilder.length() == 0;
    }

    protected String getConsumesContentType(String[] consumesContentType) {
        if (consumesContentType.length > 0) {
            return consumesContentType[0];
        } else {
            return "application/json";
        }
    }

    protected boolean isRestClass(TSComplexType tsComplexType) {
        return tsComplexType.findAnnotation(RequestMapping.class) != null;
    }


    protected abstract String[] getImplementationSpecificFieldNames();
}
