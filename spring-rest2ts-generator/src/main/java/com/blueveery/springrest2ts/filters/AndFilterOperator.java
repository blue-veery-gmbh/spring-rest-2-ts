package com.blueveery.springrest2ts.filters;

import java.util.List;
import org.slf4j.Logger;

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

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        logger.info(indentation + "{ AND FILTER");
        getJavaTypeFilters().forEach(f -> f.explain(packageClass, logger, indentation+"\t"));
        logger.info(indentation + "}");
    }

}

