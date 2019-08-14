package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.*;
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
    private GenerationContext generationContext;

    public SpringRestToTsConverter(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny && !javaClass.isInterface()){
            TSModule tsModule = moduleConverter.getTsModule(javaClass);
            String simpleName = javaClass.getSimpleName().replace("Ctrl", "Service");
            TSClass tsClass = new TSClass(simpleName, tsModule);
            tsModule.addScopedType(tsClass);
            TypeMapper.registerTsType(javaClass, tsClass);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass) {
        TSClass tsClass = (TSClass) TypeMapper.map(javaClass);

        setSupperClass(javaClass, tsClass);
        tsClass.getAnnotationList().add(javaClass.getAnnotation(RequestMapping.class));

        Map<String, TSType> typeParametersMap = new HashMap<>();
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericInterfaces()));
        typeParametersMap.putAll(createTypeParametersMap(javaClass.getGenericSuperclass()));

        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, false, true);
        tsClass.addTsMethod(tsConstructorMethod);

        for (Method method: javaClass.getMethods()) {
            if(method.getDeclaringClass() == javaClass || method.getDeclaringClass().isInterface()){
                if(isRestMethod(method)){
                    TSType fallbackTSType = TypeMapper.tsAny;
                    if(typeParametersMap.get(method.getDeclaringClass().getName()) != null){
                        fallbackTSType = typeParametersMap.get(method.getDeclaringClass().getName());
                    }

                    TSMethod tsMethod = new TSMethod(method.getName(), tsClass, TypeMapper.map(method.getGenericReturnType(), fallbackTSType), false, false);
                    for (Parameter parameter :method.getParameters()) {
                        TSParameter tsParameter = new TSParameter(parameter.getName(), TypeMapper.map(parameter.getParameterizedType(), fallbackTSType));
                        addRestAnnotations(parameter.getAnnotations(), tsParameter);
                        if (parameterIsMapped(tsParameter.getAnnotationList())) {
                            setOptional(tsParameter);
                            setAsNullableType(parameter.getParameterizedType(), parameter.getDeclaredAnnotations(), tsParameter);
                            tsMethod.getParameterList().add(tsParameter);
                        }
                    }
                    addRestAnnotations(method.getAnnotations(), tsMethod);
                    tsClass.addTsMethod(tsMethod);
                }
            }
        }

        generationContext.getImplementationGenerator(tsClass).addComplexTypeUsage(tsClass);
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

    private void addRestAnnotations(Annotation[] annotations, IAnnotated annotatedElement) {
        for(Annotation annotation:annotations){
            if (annotation.annotationType().getPackage().equals(RequestMapping.class.getPackage())) {
                annotatedElement.getAnnotationList().add(annotation);
            }
        }
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
