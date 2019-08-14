package com.blueveery.springrest2ts.tsmodel;

import java.lang.annotation.Annotation;
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
}
