package com.blueveery.springrest2ts.filters;

public class HasAnnotationJavaTypeFilter implements JavaTypeFilter {
    Class annotation;

    public HasAnnotationJavaTypeFilter(Class annotation) {
        if (!annotation.isAnnotation()) {
            throw new IllegalStateException("Annotation required");
        }
        this.annotation = annotation;
    }

    @Override
    public boolean filter(Class javaType) {
        return javaType.isAnnotationPresent(annotation);
    }
}
