package com.blueveery.springrest2ts.filters;

import java.util.List;

public class AndFilterOperator extends ComplexFilterOperator {

    public AndFilterOperator(List<JavaTypeFilter> javaTypeFilters) {
        super(javaTypeFilters);
    }

    @Override
    public boolean filter(Class javaType) {
        for (JavaTypeFilter typeFilter : getJavaTypeFilters()) {
            if (!typeFilter.filter(javaType)) {
                return false;
            }
        }
        return true;
    }

}

