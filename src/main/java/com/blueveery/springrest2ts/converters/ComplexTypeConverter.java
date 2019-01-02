package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    public abstract void preConvert(ModuleConverter moduleConverter, Class javaClass);
    public abstract void convert(Class javaClass);
}
