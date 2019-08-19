package com.blueveery.springrest2ts.tsmodel;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public interface IAnnotated {
    List<Annotation> getAnnotationList();

    default <T> T findAnnotation(Class<? extends Annotation> aClass){
        for (Annotation annotation:getAnnotationList()) {
            if(annotation.annotationType() == aClass){
                return (T) annotation;
            }
            for (Annotation baseAnnotation : annotation.annotationType().getAnnotations()) {
                if (baseAnnotation.annotationType() == aClass) {
                    return (T) baseAnnotation;
                }
            }
        }
        return null;
    }

    default Annotation findRequestMapping(){
        for (Annotation annotation:getAnnotationList()) {
            if(annotation.annotationType() == RequestMapping.class){
                return annotation;
            }
            for (Annotation baseAnnotation : annotation.annotationType().getAnnotations()) {
                if (baseAnnotation.annotationType() == RequestMapping.class) {
                    return annotation;
                }
            }
        }
        return null;
    }

    default RequestMapping getRequestMapping(){
        Annotation requestMapping = findRequestMapping();
        if (requestMapping == null) {
            return null;
        }


        RequestMappingEntity requestMappingEntity = new RequestMappingEntity();
        Class<? extends Annotation> annotationType = requestMapping.annotationType();
        switch(annotationType.getSimpleName()){
            case "RequestMapping" : requestMappingEntity.setMethod(((RequestMapping) requestMapping).method()); break;
            case "GetMapping" : requestMappingEntity.setMethod(RequestMethod.GET); break;
            case "PostMapping" : requestMappingEntity.setMethod(RequestMethod.POST); break;
            case "PutMapping" : requestMappingEntity.setMethod(RequestMethod.PUT); break;
            case "DeleteMapping" : requestMappingEntity.setMethod(RequestMethod.DELETE); break;
            case "PatchMapping" : requestMappingEntity.setMethod(RequestMethod.PATCH); break;
        }

        try {
            Method nameMethod = annotationType.getMethod("name");
            requestMappingEntity.setName((String) nameMethod.invoke(requestMapping));

            Method producesMethod = annotationType.getMethod("produces");
            requestMappingEntity.setProduces((String[]) producesMethod.invoke(requestMapping));

            Method consumesMethod = annotationType.getMethod("consumes");
            requestMappingEntity.setConsumes((String[]) consumesMethod.invoke(requestMapping));

            Method headersMethod = annotationType.getMethod("headers");
            requestMappingEntity.setHeaders((String[]) headersMethod.invoke(requestMapping));

            Method pathMethod = annotationType.getMethod("path");
            requestMappingEntity.setPath((String[]) pathMethod.invoke(requestMapping));

            Method valueMethod = annotationType.getMethod("value");
            requestMappingEntity.setValue((String[]) valueMethod.invoke(requestMapping));

            Method paramsMethod = annotationType.getMethod("params");
            requestMappingEntity.setParams((String[]) paramsMethod.invoke(requestMapping));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return requestMappingEntity;

    }
}
