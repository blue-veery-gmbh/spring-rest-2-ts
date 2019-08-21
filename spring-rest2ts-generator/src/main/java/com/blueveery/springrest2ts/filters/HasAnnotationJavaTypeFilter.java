package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class HasAnnotationJavaTypeFilter implements JavaTypeFilter {
    Class annotation;

    public HasAnnotationJavaTypeFilter(Class annotation) {
        if (!annotation.isAnnotation()) {
            throw new IllegalStateException("Annotation required");
        }
//        @Target({ElementType.METHOD, ElementType.TYPE})
//        @Retention(RetentionPolicy.RUNTIME)
//        Target targetAnnotation = (Target) annotation.getAnnotation(Target.class);
//        if (targetAnnotation != null) {
//            Arrays.binarySearch(targetAnnotation.value(), ElementType.TYPE).
//        }

        this.annotation = annotation;
    }

    @Override
    public boolean filter(Class javaType) {
        return javaType.isAnnotationPresent(annotation);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (filter(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s has annotation %s", packageClass.getSimpleName(), annotation.getSimpleName()));
        }else {
            logger.warn(indentation + String.format("FALSE => class %s doesn't have annotation %s", packageClass.getSimpleName(), annotation.getSimpleName() ));
        }
    }
}
