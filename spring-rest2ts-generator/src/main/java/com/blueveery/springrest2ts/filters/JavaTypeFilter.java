package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

public interface JavaTypeFilter {
    boolean filter(Class javaType);

    void explain(Class packageClass, Logger logger, String indentation);
}
