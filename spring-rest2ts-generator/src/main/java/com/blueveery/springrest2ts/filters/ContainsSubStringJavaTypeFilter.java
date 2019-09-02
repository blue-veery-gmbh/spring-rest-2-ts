package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class ContainsSubStringJavaTypeFilter implements JavaTypeFilter {

    private final String substring;

    public ContainsSubStringJavaTypeFilter(String substring) {
        this.substring = substring;
    }

    @Override
    public boolean accept(Class javaType) {
        return javaType.getSimpleName().contains(substring);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s simple name contains \"%s\"", packageClass.getSimpleName(), substring));
        }else {
            logger.warn(indentation + String.format("FALSE => class %s simple name doesn't contains \"%s\"", packageClass.getSimpleName(), substring));
        }
    }
}
