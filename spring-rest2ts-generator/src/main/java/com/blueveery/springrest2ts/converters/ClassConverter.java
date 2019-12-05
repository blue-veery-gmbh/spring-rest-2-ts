package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ConversionExtension;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;

import java.util.ArrayList;
import java.util.List;

public abstract class ClassConverter<C extends ConversionExtension> extends ComplexTypeConverter {
    protected List<C> conversionExtensionList = new ArrayList<>();

    protected ClassConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public ClassConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    public List<C> getConversionExtensionList() {
        return conversionExtensionList;
    }
}
