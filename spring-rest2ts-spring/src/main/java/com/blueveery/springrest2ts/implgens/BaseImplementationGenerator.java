package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.extensions.ConversionExtension;
import com.blueveery.springrest2ts.extensions.ModelSerializerExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.extensions.StandardJsonSerializerExtension;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseImplementationGenerator implements ImplementationGenerator {

    protected static final String JSON_CONTENT_TYPE = "application/json";
    protected List<? extends ConversionExtension> extensionSet;

    protected ModelSerializerExtension modelSerializerExtension = new StandardJsonSerializerExtension();

    protected List<TSParameter> findRequestBodyParam(TSMethod method) {
        return method.getParameterList()
                .stream()
                .filter(
                        p -> p.getAnnotationList()
                                .stream()
                                .anyMatch(a -> a instanceof RequestBody)
                )
                .collect(Collectors.toList());
    }

    protected abstract void initializeHttpParams(StringBuilder requestParamsBuilder, String requestParamsVar);

    protected abstract void addRequestParameter(StringBuilder requestParamsBuilder, String requestParamsVar, String queryParamVar);

    protected abstract String[] getImplementationSpecificFieldNames();

    protected BaseImplementationGenerator() {
    }

    @Override
    public void setExtensions(List<? extends ConversionExtension> conversionExtensionSet) {
        this.extensionSet = conversionExtensionSet;
    }

    @Override
    public ModelSerializerExtension getSerializationExtension() {
        return modelSerializerExtension;
    }

    @Override
    public void setSerializationExtension(ModelSerializerExtension modelSerializerExtension) {
        this.modelSerializerExtension = modelSerializerExtension;
    }

    protected void writeConstructorImplementation(BufferedWriter writer, TSClass tsClass) throws IOException {

        if (tsClass.getExtendsClass() == null) {
            StringBuilder classFieldBuilder = new StringBuilder();
            for (String name : getImplementationSpecificFieldNames()) {
                classFieldBuilder.append("    this." + name + " = " + name + ";\n");
            }
            String classField = classFieldBuilder.toString();
            writer.write(classField.length() > 0 ? classField.substring(0,classField.length()-1): "");
        } else {
            writer.write("super(");
            writer.write(String.join(",", getImplementationSpecificFieldNames()));
            writer.write(");");
        }
    }

    protected String getPathFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping != null) {
            if (requestMapping.path().length > 0) {
                return requestMapping.path()[0];
            }
            if (requestMapping.value().length > 0) {
                return requestMapping.value()[0];
            }
        }
        return "";
    }

    protected void replaceInStringBuilder(StringBuilder pathStringBuilder, String pathVariableName, TSParameter tsParameter) {
        final String captureTheRest = "{*" + pathVariableName + "}";
        final String captureVariable = "{" + pathVariableName + "}";

        stripPatternParserFeatures(pathStringBuilder);
        
        int startCaptureVariable = pathStringBuilder.lastIndexOf(captureVariable);
        int startCaptureTheRest = pathStringBuilder.lastIndexOf(captureTheRest);
        
        if (startCaptureTheRest != -1) {
            if (tsParameter.getType() instanceof TSArray) {
                pathStringBuilder.replace(startCaptureTheRest,
                    startCaptureTheRest + captureTheRest.length(),
                    "' + " + pathVariableName +
                    ".flatMap(x=>encodeURIComponent(x)).join('/') + '");
            } else {
                pathStringBuilder.replace(startCaptureTheRest,
                    startCaptureTheRest + captureTheRest.length(),
                    "' + encodeURIComponent(" + pathVariableName + ") + '");
            }
        } else if (startCaptureVariable != -1) {
            pathStringBuilder.replace(startCaptureVariable,
                startCaptureVariable + captureVariable.length(),
                "' + encodeURIComponent(" + pathVariableName + ") + '");
        } else {
            validatePathVariableParameter(pathStringBuilder, pathVariableName, -1);  
        }

    }

    protected static void stripPatternParserFeatures(StringBuilder pathStringBuilder)
    {
      stripPatternParserFeatures(pathStringBuilder, 0);
    }

    protected static void stripPatternParserFeatures(StringBuilder pathStringBuilder, int fromIndex)
    {
        int bracketStartIndex = pathStringBuilder.indexOf("{", fromIndex);
        if (bracketStartIndex == -1) {
            return;
        }
        int bracketEndIndex = unescapedIndexOf(pathStringBuilder, "}", bracketStartIndex + 1);
        if (bracketStartIndex + 1 < pathStringBuilder.length()) {
            int innerBracketStartIndex = unescapedIndexOf(pathStringBuilder, "{", bracketStartIndex + 1);
            while(bracketEndIndex > 0 && (bracketEndIndex + 1 < pathStringBuilder.length()) &&
                innerBracketStartIndex > bracketStartIndex &&
                innerBracketStartIndex < bracketEndIndex) {
                innerBracketStartIndex = unescapedIndexOf(pathStringBuilder, "{", innerBracketStartIndex + 1);
                bracketEndIndex  = unescapedIndexOf(pathStringBuilder, "}", bracketEndIndex + 1);
            }
        }
        if (bracketEndIndex == -1) {
            return;
        }
        int colonIndex = pathStringBuilder.indexOf(":", bracketStartIndex);
        if (colonIndex > bracketEndIndex) {
            stripPatternParserFeatures(pathStringBuilder, bracketEndIndex);
        } else  if (colonIndex < bracketEndIndex && colonIndex > bracketStartIndex) {
            pathStringBuilder.replace(colonIndex, bracketEndIndex, "");
            stripPatternParserFeatures(pathStringBuilder, fromIndex);
        }
    }

    private static int unescapedIndexOf(StringBuilder pathStringBuilder, final String character, int fromIndex) {
        int characterIndex = pathStringBuilder.indexOf(character, fromIndex);
        while(characterIndex > 0 && (characterIndex + 1 < pathStringBuilder.length()) &&
            pathStringBuilder.charAt(characterIndex - 1) == '\\') {
            characterIndex = pathStringBuilder.indexOf(character, characterIndex + 1);
        }
        return characterIndex;
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

    protected void assignMethodParameters(
            TSMethod method, String requestParamsVar, StringBuilder pathStringBuilder,
            StringBuilder requestBodyBuilder, StringBuilder requestParamsBuilder
    ) {
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
                addPathVariable(pathStringBuilder, tsParameter, pathVariable);
                continue;
            }
            RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
            if (requestParam != null) {
                String requestParamName = getRequestParamName(tsParameter, requestParam);
                boolean isNullableType = tsParameter.isNullable();
                if (tsParameter.isOptional() || isNullableType) {
                    queryParamsListBuilder
                            .append("\n")
                            .append("    if (")
                            .append(tsParameterName)
                            .append(" !== undefined && ")
                            .append(tsParameterName)
                            .append(" !== null) {\n");
                    queryParamsListBuilder.append(String.format("      %s.push({name: '%s', value: %s});\n", queryParamsListVar, requestParamName, callToStringOnParameterIfRequired(tsParameter)));
                    queryParamsListBuilder.append("    }\n");
                } else {
                    queryParamsListBuilder.append(String.format("\n    %s.push({name: '%s', value: %s});\n  ", queryParamsListVar, requestParamName, callToStringOnParameterIfRequired(tsParameter)));
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
        queryParamsListBuilder.insert(0, "    const " + queryParamsListVar + ": { name: string, value: string }[] = [];");
        requestParamsBuilder.append(queryParamsListBuilder);
        initializeHttpParams(requestParamsBuilder, requestParamsVar);
        String queryParamVar = "queryParam";
        requestParamsBuilder.append(String.format("\n    for (const %s of %s) {", queryParamVar, queryParamsListVar));
        addRequestParameter(requestParamsBuilder, requestParamsVar, queryParamVar);
        requestParamsBuilder.append("\n    }\n");
    }

    private String getRequestParamName(TSParameter tsParameter, RequestParam requestParam) {
        String requestParamName = requestParam.value();
        if ("".equals(requestParamName)) {
            requestParamName = tsParameter.getName();
        }
        return requestParamName;
    }

    protected void addPathVariable(StringBuilder pathStringBuilder, TSParameter tsParameter, PathVariable pathVariable) {
        String variableName = pathVariable.value();
        if ("".equals(variableName)){
            variableName = pathVariable.name();
        }

        if ("".equals(variableName)) {
            variableName = tsParameter.getName();
        }

        replaceInStringBuilder(pathStringBuilder, variableName, tsParameter);
    }

    protected boolean isStringBuilderEmpty(StringBuilder requestParamsBuilder) {
        return requestParamsBuilder.length() == 0;
    }

    protected String getContentType(String[] contentTypes) {
        if (contentTypes.length > 0) {
            return contentTypes[0];
        } else {
            return JSON_CONTENT_TYPE;
        }
    }

    protected boolean isRestClass(TSComplexElement tsComplexType) {
        return tsComplexType.findAnnotation(RequestMapping.class) != null;
    }

    protected String getEndpointPath(RequestMapping methodRequestMapping, RequestMapping classRequestMapping) {
        String classLevelPath = getPathFromRequestMapping(classRequestMapping);
        String methodLevelPath = getPathFromRequestMapping(methodRequestMapping);
        validateParsePattern(classLevelPath);
        validateParsePattern(methodLevelPath);
        String pathSeparator = "";
        if (!classLevelPath.endsWith("/") && !(methodLevelPath.startsWith("/") || "".equals(methodLevelPath))) {
            pathSeparator="/";
        }
        return classLevelPath + pathSeparator +  methodLevelPath + "'";
    }

    protected boolean bodyIsAllowedInRequest(String httpMethod) {
        return "PUT".equals(httpMethod) || "POST".equals(httpMethod) || "PATCH".equals(httpMethod);
    }
    
    private static void validateParsePattern(final String pattern) {
        if (!"".equals(pattern)) {
            try {
                new PathPatternParser().parse(pattern);
            } catch (PatternParseException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
