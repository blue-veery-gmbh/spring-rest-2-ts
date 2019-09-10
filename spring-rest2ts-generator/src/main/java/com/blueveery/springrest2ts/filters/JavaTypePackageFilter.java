package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class JavaTypePackageFilter implements JavaTypeFilter {

    private Package targetPackage;

    public JavaTypePackageFilter(Package targetPackage) {
        this.targetPackage = targetPackage;
    }

    @Override
    public boolean accept(Class javaType) {
        return javaType.getPackage().equals(targetPackage);
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        if (accept(packageClass)) {
            logger.info(indentation + String.format("TRUE => class %s package matches target package \"%s\"", packageClass.getSimpleName(), targetPackage.getName()));
        } else {
            logger.warn(indentation + String.format("FALSE => class %s package doesn't match target package \"%s\"", packageClass.getSimpleName(), targetPackage.getName()));
        }
    }
}
