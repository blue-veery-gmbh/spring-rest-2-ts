package com.blueveery.springrest2ts.filters;

public class BaseClassJavaTypeFilter implements JavaTypeFilter {
    private Class baseType;

    public BaseClassJavaTypeFilter(Class baseType) {
        if (baseType.isAnnotation()) {
            throw new IllegalStateException("Annotation could not be a base Type");
        }
        this.baseType = baseType;
    }

    @Override
    public boolean filter(Class javaType) {
        return baseType.isAssignableFrom(javaType);
    }
}
