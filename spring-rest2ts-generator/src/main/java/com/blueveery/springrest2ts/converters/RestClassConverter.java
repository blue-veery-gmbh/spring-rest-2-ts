package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;

public abstract class RestClassConverter extends ClassConverter<RestConversionExtension> {
    protected RestClassConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public RestClassConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }
}
