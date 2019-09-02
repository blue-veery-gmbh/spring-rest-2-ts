package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class RegexpJavaTypeFilter implements JavaTypeFilter {

    private final String pattern;

    public RegexpJavaTypeFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(Class javaType) {
        return javaType.getSimpleName().matches(pattern);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s simple name matches regex \"%s\"", packageClass.getSimpleName(), pattern ));
        }else {
            logger.warn(indentation + String.format("FALSE => class %s simple name doesn't matches regex \"%s\"", packageClass.getSimpleName(), pattern));
        }
    }
}
