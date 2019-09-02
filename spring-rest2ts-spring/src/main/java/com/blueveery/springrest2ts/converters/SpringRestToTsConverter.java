package com.blueveery.springrest2ts.converters;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void convert(Class javaClass, NullableTypeStrategy nullableTypeStrategy) {
        TSClass tsClass = (TSClass) TypeMapper.map(javaClass);

        setSupperClass(javaClass, tsClass);
        tsClass.addAllAnnotations(javaClass.getAnnotations());

        Map<String, TSType> typeParametersMap = new HashMap<>();
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericInterfaces()));
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericSuperclass()));

        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, implementationGenerator, false, true);
        tsClass.addTsMethod(tsConstructorMethod);

        for (Method method: javaClass.getMethods()) {
            if(method.getDeclaringClass() == javaClass || method.getDeclaringClass().isInterface()){
                if(isRestMethod(method)){
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
                    TSMethod tsMethod = new TSMethod(method.getName(), tsClass, TypeMapper.map(genericReturnType, fallbackTSType), implementationGenerator, false, false);
                    for (Parameter parameter :method.getParameters()) {
                        TSParameter tsParameter = new TSParameter(parameter.getName(), TypeMapper.map(parameter.getParameterizedType(), fallbackTSType), implementationGenerator);
                        tsParameter.addAllAnnotations(parameter.getAnnotations());
                        if (parameterIsMapped(tsParameter.getAnnotationList())) {
                            setOptional(tsParameter);
                            nullableTypeStrategy.setAsNullableType(parameter.getParameterizedType(), parameter.getDeclaredAnnotations(), tsParameter);
                            tsMethod.getParameterList().add(tsParameter);
                        }
                    }
                    tsMethod.addAllAnnotations(method.getAnnotations());
                    tsClass.addTsMethod(tsMethod);
                    conversionListener.tsMethodCreated(method, tsMethod);
                }
            }
        }

        implementationGenerator.addComplexTypeUsage(tsClass);
        conversionListener.tsScopedTypeCreated(javaClass, tsClass);
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
