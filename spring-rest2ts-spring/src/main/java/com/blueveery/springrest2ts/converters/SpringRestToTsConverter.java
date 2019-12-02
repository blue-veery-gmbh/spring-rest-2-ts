package com.blueveery.springrest2ts.converters;

import static com.blueveery.springrest2ts.spring.RequestMappingUtility.getRequestMapping;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny && !javaClass.isInterface()){
            TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
            String tsClassName = createTsClassName(javaClass);
            TSClass tsClass = new TSClass(tsClassName, tsModule, implementationGenerator);
            tsModule.addScopedType(tsClass);
            TypeMapper.registerTsType(javaClass, tsClass);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSClassReference tsClassReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsClassReference.getReferencedType();

        convertFormalTypeParameters(javaClass.getTypeParameters(), tsClassReference);
        setSupperClass(javaClass, tsClass);
        tsClass.addAllAnnotations(javaClass.getAnnotations());

        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, implementationGenerator, false, true);
        tsClass.addTsMethod(tsConstructorMethod);
        List<Method> restMethodList = filterRestMethods(javaClass);
        Map<Method, StringBuilder> methodNamesMap = new HashMap<>();

        for (Method method: restMethodList) {

            Map<String, Type> variableNameToJavaType = new HashMap<>();
            Class<?> declaringClass = method.getDeclaringClass();
            if(declaringClass != javaClass && method.getDeclaringClass().isInterface()){
                variableNameToJavaType = fillVariableNameToJavaType(javaClass, declaringClass);
            }

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {// handling ResponseEntity
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                if (parameterizedType.getRawType() == ResponseEntity.class) {
                    genericReturnType = parameterizedType.getActualTypeArguments()[0];
                }
            }
            String methodName = mapMethodName(restMethodList, methodNamesMap,  method);
            TSType methodReturnType = TypeMapper.map(resolveTypeVariable(genericReturnType, variableNameToJavaType));
            tsClass.getModule().scopedTypeUsage(methodReturnType);
            TSMethod tsMethod = new TSMethod(methodName, tsClass, methodReturnType, implementationGenerator, false, false);
            for (Parameter parameter:method.getParameters()) {
                Type parameterType = resolveTypeVariable(parameter.getParameterizedType(), variableNameToJavaType);
                TSParameter tsParameter = new TSParameter(parameter.getName(), TypeMapper.map(parameterType), implementationGenerator);
                tsParameter.addAllAnnotations(parameter.getAnnotations());
                if (parameterIsMapped(tsParameter)) {
                    tsClass.getModule().scopedTypeUsage(tsParameter.getType());
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

    private Type resolveTypeVariable(Type type, Map<String, Type> variableNameToJavaType) {
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            Type resolvedType = variableNameToJavaType.get(typeVariable.getName());
            if (resolvedType != null) {
                return resolvedType;
            }
        }
        return type;
    }

    private Map<String, Type> fillVariableNameToJavaType(Class javaClass, Class<?> declaringClass) {
        Map<String, Type> typeParametersMap = new HashMap<>();
        for (Type type:javaClass.getGenericInterfaces()){
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == declaringClass) {
                    for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                        TypeVariable typeParameter = declaringClass.getTypeParameters()[i];
                        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[i];
                        typeParametersMap.put(typeParameter.getName(), actualTypeArgument);
                    }
                    return typeParametersMap;
                }
                return fillVariableNameToJavaType((Class) type, declaringClass);
            }
        }
        return typeParametersMap;
    }

    private List<Method> filterRestMethods(Class javaClass) {
        List<Method> restMethodList = new ArrayList<>();
        for (Method method : javaClass.getMethods()) {
            if(method.getDeclaringClass() == javaClass || method.getDeclaringClass().isInterface()) {
                for (Method nextMethod : javaClass.getSuperclass().getMethods()) {
                    if(nextMethod.equals(method)){
                        continue; // parent class contains method from interface
                    }
                }
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
                    methodName.append(pathComponent.toUpperCase().replace("-", "_"));
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

    private boolean parameterIsMapped(TSParameter tsParameter) {
        for (Annotation annotation : tsParameter.getAnnotationList()) {
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

        if (tsParameter.getType() instanceof TSInterfaceReference) {
            TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) tsParameter.getType();
            for (Class nextClass : tsInterfaceReference.getReferencedType().getMappedFromJavaTypeSet()) {
                if (nextClass.isAssignableFrom(Pageable.class)) {
                    return true;
                }
            }
        }
        return false;
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
        TSType tsSupperClass = TypeMapper.map(javaType.getAnnotatedSuperclass().getType());
        if(tsSupperClass instanceof TSClassReference){
            TSClassReference tsClassReference = (TSClassReference) tsSupperClass;
            convertFormalTypeParameters(javaType.getTypeParameters(), tsClassReference);
            tsClass.setExtendsClass(tsClassReference);
            tsClass.addScopedTypeUsage(tsClassReference);
        }
    }
}
