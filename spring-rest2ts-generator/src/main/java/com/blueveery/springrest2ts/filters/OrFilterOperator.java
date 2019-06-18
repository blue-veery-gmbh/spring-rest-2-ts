package com.blueveery.springrest2ts.filters;

import java.util.List;

public class OrFilterOperator extends ComplexFilterOperator {

    public OrFilterOperator(List<JavaTypeFilter> javaTypeFilters) {
        super(javaTypeFilters);
    }

    @Override
    public boolean filter(Class javaType) {
        for (JavaTypeFilter typeFilter : getJavaTypeFilters()) {
            if (typeFilter.filter(javaType)) {
                return true;
            }
        }
        return false;
    }

}
