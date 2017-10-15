package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SpringRestToTsConverter extends ComplexTypeConverter{

    public void preConvert(Map<String, TSModule> modulesMap, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny && !javaClass.isInterface()){
            TSModule tsModule = modulesMap.get(javaClass.getPackage().getName());
            TSClass tsClass = new TSClass(javaClass.getSimpleName(), tsModule);
            tsModule.addScopedType(tsClass);
            TypeMapper.registerTsType(javaClass, tsClass);
        }

    }

    @Override
    public void convert(Map<String, TSModule> modulesMap, Class javaType, ImplementationGenerator implementationGenerator) {
        TSClass tsClass = (TSClass) TypeMapper.map(javaType);

        setSupperClass(javaType, tsClass);
        tsClass.getAnnotationList().add(javaType.getAnnotation(RequestMapping.class));

        Map<String, TSType> typeParametersMap = new HashMap<>();
        typeParametersMap.putAll(createTypeParametersMap(javaType.getGenericInterfaces()));
        typeParametersMap.putAll(createTypeParametersMap(javaType.getGenericSuperclass()));

        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, false, true);
        tsClass.addTsMethod(tsConstructorMethod);

        for (Method method:javaType.getMethods()) {
            if(method.getDeclaringClass() == javaType || method.getDeclaringClass().isInterface()){
                if(isRestMethod(method)){
                    TSType fallbackTSType = TypeMapper.tsAny;
                    if(typeParametersMap.get(method.getDeclaringClass().getName()) != null){
                        fallbackTSType = typeParametersMap.get(method.getDeclaringClass().getName());
                    }

                    TSMethod tsMethod = new TSMethod(method.getName(), tsClass, TypeMapper.map(method.getGenericReturnType(), fallbackTSType), false, false);
                    for (Parameter parameter :method.getParameters()) {
                        TSParameter tsParameter = new TSParameter(parameter.getName(), TypeMapper.map(parameter.getParameterizedType(), fallbackTSType));
                        addRestAnnotations(parameter.getAnnotations(), tsParameter);
                        tsMethod.getParameterList().add(tsParameter);
                    }
                    addRestAnnotations(method.getAnnotations(), tsMethod);
                    tsClass.addTsMethod(tsMethod);
                }
            }
        }

        implementationGenerator.addComplexTypeUsage(tsClass);

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

    private void addRestAnnotations(Annotation[] annotations, IAnnotated tsMethod) {
        for(Annotation annotation:annotations){
            if (annotation.annotationType().getPackage().equals(RequestMapping.class.getPackage())) {
                tsMethod.getAnnotationList().add(annotation);
            }
        }
    }

    private boolean isRestMethod(Method method) {
        return method.isAnnotationPresent(RequestMapping.class);
    }

    private void setSupperClass(Class javaType, TSClass tsClass) {
        TSType tsSupperClass = TypeMapper.map(javaType.getSuperclass());
        if(tsSupperClass instanceof TSClass){
            tsClass.setExtendsClass((TSClass) tsSupperClass);
        }
    }
}
