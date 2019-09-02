package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
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

    public abstract boolean preConverted(ModuleConverter moduleConverter, Class javaClass);
    public abstract void convert(Class javaClass, NullableTypeStrategy nullableTypeStrategy);

    public ClassNameMapper getClassNameMapper() {
        return classNameMapper;
    }

    public void setClassNameMapper(ClassNameMapper classNameMapper) {
        this.classNameMapper = classNameMapper;
    }
}
