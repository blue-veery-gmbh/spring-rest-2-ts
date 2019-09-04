package com.blueveery.springrest2ts.converters;

import static com.blueveery.springrest2ts.spring.RequestMappingUtility.getRequestMapping;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SpringRestToTsConverter extends ComplexTypeConverter{

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny && !javaClass.isInterface()){
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            String simpleName = classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName());
            TSClass tsClass = new TSClass(simpleName, tsModule, implementationGenerator);
            tsModule.addScopedType(tsClass);
            TypeMapper.registerTsType(javaClass, tsClass);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSClass tsClass = (TSClass) TypeMapper.map(javaClass);

        setSupperClass(javaClass, tsClass);
        tsClass.addAllAnnotations(javaClass.getAnnotations());

        Map<String, TSType> typeParametersMap = new HashMap<>();
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericInterfaces()));
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericSuperclass()));

        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, implementationGenerator, false, true);
        tsClass.addTsMethod(tsConstructorMethod);
        List<Method> restMethodList = filterRestMethods(javaClass);
        Map<Method, StringBuilder> methodNamesMap = new HashMap<>();

        for (Method method: restMethodList) {
            TSType fallbackTSType = TypeMapper.tsAny;
            if(typeParametersMap.get(method.getDeclaringClass().getName()) != null){
                fallbackTSType = typeParametersMap.get(method.getDeclaringClass().getName());
            }

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                if (parameterizedType.getRawType() == ResponseEntity.class) {
                    genericReturnType = parameterizedType.getActualTypeArguments()[0];
                }
            }
            String methodName = mapMethodName(restMethodList, methodNamesMap,  method);
            TSMethod tsMethod = new TSMethod(methodName, tsClass, TypeMapper.map(genericReturnType, fallbackTSType), implementationGenerator, false, false);
            for (Parameter parameter:method.getParameters()) {
                TSParameter tsParameter = new TSParameter(parameter.getName(), TypeMapper.map(parameter.getParameterizedType(), fallbackTSType), implementationGenerator);
                tsParameter.addAllAnnotations(parameter.getAnnotations());
                if (parameterIsMapped(tsParameter.getAnnotationList())) {
                    setOptional(tsParameter);
                    nullableTypesStrategy.setAsNullableType(parameter.getParameterizedType(), parameter.getDeclaredAnnotations(), tsParameter);
                    tsMethod.getParameterList().add(tsParameter);
                }
            }
            tsMethod.addAllAnnotations(method.getAnnotations());
            tsClass.addTsMethod(tsMethod);
            conversionListener.tsMethodCreated(method, tsMethod);
        }

        implementationGenerator.addComplexTypeUsage(tsClass);
        conversionListener.tsScopedTypeCreated(javaClass, tsClass);
    }

    private List<Method> filterRestMethods(Class javaClass) {
        List<Method> restMethodList = new ArrayList<>();
        for (Method method : javaClass.getMethods()) {
            if(method.getDeclaringClass() == javaClass || method.getDeclaringClass().isInterface()) {
                if (isRestMethod(method)) {
                    restMethodList.add(method);
                }
            }
        }
        return restMethodList;
    }

    private String mapMethodName(List<Method> restMethodList, Map<Method, StringBuilder> methodNamesMap, Method currentMethod) {
        StringBuilder methodName = methodNamesMap.get(currentMethod);
        if (methodName != null) {
            return methodName.toString();
        }
        List<Method> overloadedMethods = restMethodList.stream().filter(m -> m.getName().equals(currentMethod.getName())).collect(Collectors.toList());
        if (overloadedMethods.size() == 1) {
            return currentMethod.getName();
        }
        Map<Method, StringBuilder> overloadedMethodNamesMap = new HashMap<>();
        Map<Method, RequestMapping> methodsRequestMappingsMap = new HashMap<>();
        for (Method m : overloadedMethods) {
            overloadedMethodNamesMap.put(m, new StringBuilder(m.getName()));
            methodsRequestMappingsMap.put(m, getRequestMapping(Arrays.asList (m.getDeclaredAnnotations())));
        }


        appendHttpMethodToMethodName(overloadedMethodNamesMap, methodsRequestMappingsMap);
        if(!methodNamesAreUnique(overloadedMethodNamesMap)){
            appendHttpPathToMethodName(overloadedMethodNamesMap, methodsRequestMappingsMap);
        }
        if(!methodNamesAreUnique(overloadedMethodNamesMap)){
            logger.error("There are overloaded REST methods which names are not unique after appending http method and http path : " + currentMethod);
        }
        overloadedMethodNamesMap.forEach((method, name) -> methodNamesMap.put(method, name));
        return overloadedMethodNamesMap.get(currentMethod).toString();
    }

    private void appendHttpMethodToMethodName(Map<Method, StringBuilder> overloadedMethodNamesMap, Map<Method, RequestMapping> methodsRequestMappingsMap) {
        Map<Method, String> httpMethodsValuesMap = new HashMap<>();

        for (Map.Entry<Method, RequestMapping> entry : methodsRequestMappingsMap.entrySet()) {
            Method key = entry.getKey();
            RequestMapping value = entry.getValue();
            SortedSet<String> httpMethodsSet = new TreeSet<>();
            for (RequestMethod requestMethod : value.method()) {
                httpMethodsSet.add(requestMethod.name());
            }
            httpMethodsValuesMap.put(key, String.join("_", httpMethodsSet));
        }

        Set<String> allDifferentHttpMethods = new HashSet<>(httpMethodsValuesMap.values());
        if (allDifferentHttpMethods.size() <= 1) {
            return;
        }
        overloadedMethodNamesMap.forEach((method, methodName) -> methodName.append(httpMethodsValuesMap.get(method)));
    }

    private void appendHttpPathToMethodName(Map<Method, StringBuilder> overloadedMethodNamesMap, Map<Method, RequestMapping> methodsRequestMappingsMap) {
        Map<Method, String[]> methodsHttpPaths = new HashMap<>();
        for (Map.Entry<Method, RequestMapping> entry : methodsRequestMappingsMap.entrySet()) {
            Method method = entry.getKey();
            RequestMapping requestMapping = entry.getValue();
            if (requestMapping.path().length > 0) {
                methodsHttpPaths.put(method, requestMapping.path()[0].split("/"));
            }else{
                methodsHttpPaths.put(method, new String[]{""});
            }
        }

        int startIndex = findPathCommonPrefixIndex(methodsHttpPaths);

        for (Method method : overloadedMethodNamesMap.keySet()) {
            StringBuilder methodName = overloadedMethodNamesMap.get(method);
            String[] pathComponents = methodsHttpPaths.get(method);
            for (int i = startIndex; i < pathComponents.length; i++) {
                String pathComponent = pathComponents[i];
                if(!pathComponent.contains("{") && !"".equals(pathComponent)) {
                    methodName.append("_");
                    methodName.append(pathComponent.toUpperCase());
                }
            }
        }

    }

    private int findPathCommonPrefixIndex(Map<Method, String[]> methodsHttpPaths) {
        return 0;
    }

    private boolean methodNamesAreUnique(Map<Method, StringBuilder> overloadedMethodNamesMap) {
        Set<String> methodNames = new HashSet<>();
        overloadedMethodNamesMap.forEach((k, v) -> methodNames.add(v.toString()));
        return overloadedMethodNamesMap.size() == methodNames.size();
    }

    private void setOptional(TSParameter tsParameter) {
        for (Annotation annotation : tsParameter.getAnnotationList()) {
            if(annotation instanceof PathVariable){
                PathVariable pathVariable = (PathVariable) annotation;
                tsParameter.setOptional(!pathVariable.required());
                return;
            }
            if(annotation instanceof RequestParam){
                RequestParam requestParam = (RequestParam) annotation;
                tsParameter.setOptional(!requestParam.required());
                if(!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                    tsParameter.setDefaultValue(requestParam.defaultValue());
                }
                return;
            }
            if(annotation instanceof RequestBody) {
                RequestBody requestBody = (RequestBody) annotation;
                tsParameter.setOptional(!requestBody.required());
                return;
            }
       }
    }

    private boolean parameterIsMapped(List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if(annotation instanceof PathVariable){
                return true;
            }
            if(annotation instanceof RequestParam){
                return true;
            }
            if(annotation instanceof RequestBody) {
                return true;
            }
        }
        return false;
    }

    private Map<String,TSType> createTypeParametersMap(Type... genericInterfaces) {
        Map<String, TSType> typeParametersMap = new HashMap<>();
        for (Type type:genericInterfaces){
            if(type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                typeParametersMap.put(parameterizedType.getRawType().getTypeName(), TypeMapper.map(parameterizedType.getActualTypeArguments()[0]));
            }
        }
        return typeParametersMap;
    }

    private boolean isRestMethod(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return true;
        }
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
                return true;
            }
        }
        return false;
    }

    private void setSupperClass(Class javaType, TSClass tsClass) {
        TSType tsSupperClass = TypeMapper.map(javaType.getSuperclass());
        if(tsSupperClass instanceof TSClass){
            tsClass.setExtendsClass((TSClass) tsSupperClass);
        }
    }
}
