package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

public class HasAnnotationJavaTypeFilter implements JavaTypeFilter {
    Class annotation;

    public HasAnnotationJavaTypeFilter(Class annotation) {
        if (!annotation.isAnnotation()) {
            throw new IllegalStateException("Annotation required");
        }

        Target targetAnnotation = (Target) annotation.getAnnotation(Target.class);
        if (targetAnnotation != null && Arrays.binarySearch(targetAnnotation.value(), ElementType.TYPE) == -1) {
            throw new IllegalStateException(annotation.getSimpleName() + " is not a type annotation");
        }

        Retention retentionAnnotation = (Retention) annotation.getAnnotation(Retention.class);
        if (retentionAnnotation != null && retentionAnnotation.value() != RetentionPolicy.RUNTIME) {
            throw new IllegalStateException(annotation.getSimpleName() + " is not a runtime annotation");
        }

        this.annotation = annotation;
    }

    @Override
    public boolean accept(Class javaType) {
        return javaType.isAnnotationPresent(annotation);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s has annotation %s", packageClass.getSimpleName(), annotation.getSimpleName()));
        }else {
            logger.warn(indentation + String.format("FALSE => class %s doesn't have annotation %s", packageClass.getSimpleName(), annotation.getSimpleName() ));
        }
    }
}
