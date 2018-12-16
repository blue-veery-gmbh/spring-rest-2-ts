package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Map;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    public abstract void preConvert(Map<String, TSModule> modulesMap, Class javaClass);
    public abstract void convert(Map<String, TSModule> modulesMap, Class aClass, GenerationContext generationContext);
}
