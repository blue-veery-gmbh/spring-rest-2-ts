package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeFilter;

import java.util.Map;
import java.util.Set;

public interface ConversionExtension extends ConversionListener{
    JavaTypeFilter getModelClassesJavaTypeFilter();
    JavaTypeFilter getRestClassesJavaTypeFilter();
    Set<String> getAdditionalJavaPackages();
    Map<String, ObjectMapper> getObjectMapperMap();
    boolean isMappedRestParam(Class aClass);
}
