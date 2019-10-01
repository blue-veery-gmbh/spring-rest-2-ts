package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class NotJavaTypeFilter implements JavaTypeFilter {
    JavaTypeFilter javaTypeFilter;

    public NotJavaTypeFilter(JavaTypeFilter javaTypeFilter) {
        this.javaTypeFilter = javaTypeFilter;
    }

    @Override
    public boolean accept(Class javaType) {
        return !javaTypeFilter.accept(javaType);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + "TRUE => {NOT ");
            javaTypeFilter.explain(packageClass, logger, indentation);
            logger.info(indentation + "}");
        }else {
            logger.info(indentation + "FALSE => {NOT ");
            javaTypeFilter.explain(packageClass, logger, indentation);
            logger.info(indentation + "}");

        }
    }
}

