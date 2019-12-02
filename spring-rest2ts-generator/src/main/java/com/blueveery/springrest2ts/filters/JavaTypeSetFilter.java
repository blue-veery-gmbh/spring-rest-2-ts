package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

import java.util.Set;

public class JavaTypeSetFilter implements JavaTypeFilter {

    private Set<Class> classSet;

    public JavaTypeSetFilter(Set<Class> classSet) {
        this.classSet = classSet;
    }

    @Override
    public boolean accept(Class javaType) {
        return classSet.contains(javaType);
    }

    @Override
    public void explain(Class javaType, Logger logger, String indentation) {
        if (accept(javaType)) {
            logger.info(indentation + String.format("TRUE => class %s is in the required set", javaType.getSimpleName()));
        } else {
            logger.warn(indentation + String.format("FALSE => class %s is not in the required set", javaType.getSimpleName()));
        }
    }
}
