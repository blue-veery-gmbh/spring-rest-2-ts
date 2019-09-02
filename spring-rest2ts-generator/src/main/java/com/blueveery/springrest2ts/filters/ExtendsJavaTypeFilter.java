package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class ExtendsJavaTypeFilter implements JavaTypeFilter {
    private Class baseType;

    public ExtendsJavaTypeFilter(Class baseType) {
        if (baseType.isAnnotation()) {
            throw new IllegalStateException("Annotation could not be a base Type");
        }
        this.baseType = baseType;
    }

    @Override
    public boolean accept(Class javaType) {
        return baseType.isAssignableFrom(javaType);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s extends type %s", packageClass.getSimpleName(), baseType.getSimpleName() ));
        }else {
            logger.warn(indentation + String.format("FALSE => class %s doesn't extends base type %s", packageClass.getSimpleName(), baseType.getSimpleName()));
        }
    }
}
