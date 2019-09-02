package com.blueveery.springrest2ts.filters;

import java.util.List;
import org.slf4j.Logger;

public class OrFilterOperator extends ComplexFilterOperator {

    public OrFilterOperator(List<JavaTypeFilter> javaTypeFilters) {
        super(javaTypeFilters);
    }

    @Override
    public boolean accept(Class javaType) {
        for (JavaTypeFilter typeFilter : getJavaTypeFilters()) {
            if (typeFilter.accept(javaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        logger.info(indentation + "{ OR FILTER");
        getJavaTypeFilters().forEach(f -> f.explain(packageClass, logger, indentation+"\t"));
        logger.info(indentation + "}");
    }

}
