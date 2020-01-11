package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

public class DispatcherConversionListener implements ConversionListener {

    private Set<ConversionListener> conversionListenerSet = new HashSet<>();

    public Set<ConversionListener> getConversionListenerSet() {
        return conversionListenerSet;
    }

    @Override
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        conversionListenerSet.forEach(l -> l.tsScopedTypeCreated(javaType, tsScopedElement));
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        conversionListenerSet.forEach(l -> l.tsFieldCreated(property, tsField));
    }

    @Override
    public void tsMethodCreated(Method method, TSMethod tsMethod) {
        conversionListenerSet.forEach(l -> l.tsMethodCreated(method, tsMethod));
    }

    @Override
    public void tsParameterCreated(Parameter parameter, TSParameter tsParameter) {
        conversionListenerSet.forEach(l -> l.tsParameterCreated(parameter, tsParameter));
    }
}
