package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    protected static Logger logger = LoggerFactory.getLogger("gen-logger");
    protected DispatcherConversionListener conversionListener = new DispatcherConversionListener();
    protected ImplementationGenerator implementationGenerator;
    protected ClassNameMapper classNameMapper = new NoChangeClassNameMapper();

    protected ComplexTypeConverter(ImplementationGenerator implementationGenerator) {
        this.implementationGenerator = implementationGenerator;
    }

    public ComplexTypeConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        this(implementationGenerator);
        this.classNameMapper = classNameMapper;
    }

    public DispatcherConversionListener getConversionListener() {
        return conversionListener;
    }

    public abstract boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass);
    public abstract void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy);

    public ClassNameMapper getClassNameMapper() {
        return classNameMapper;
    }

    public void setClassNameMapper(ClassNameMapper classNameMapper) {
        this.classNameMapper = classNameMapper;
    }
}
