package com.blueveery.springrest2ts.filters;

import java.util.List;

public abstract class ComplexFilterOperator implements JavaTypeFilter {

    private List<JavaTypeFilter> javaTypeFilters;

    public ComplexFilterOperator(List<JavaTypeFilter> javaTypeFilters) {
        if (javaTypeFilters.isEmpty()) {
            throw new IllegalArgumentException("Filters list is empty");
        }
        this.javaTypeFilters = javaTypeFilters;
    }

    public List<JavaTypeFilter> getJavaTypeFilters() {
        return javaTypeFilters;
    }
}
