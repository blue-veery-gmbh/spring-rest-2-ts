package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public class RejectJavaTypeFilter implements JavaTypeFilter {
    @Override
    public boolean accept(Class javaType) {
        return false;
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        logger.error(indentation + String.format("Rejecting class $s due to reject filter, please configure filter", packageClass));
    }
}
