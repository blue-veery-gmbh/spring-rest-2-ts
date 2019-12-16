package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.converters.ConversionListener;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;

import java.util.Collections;
import java.util.Set;

public interface ConversionExtension extends ConversionListener {
    default JavaTypeFilter getJavaTypeFilter() {
        return null;
    }

    default Set<String> getAdditionalJavaPackages() {
        return Collections.emptySet();
    }
}
