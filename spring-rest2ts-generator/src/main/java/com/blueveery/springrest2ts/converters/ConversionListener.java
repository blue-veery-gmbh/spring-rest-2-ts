package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ConversionListener {
    default void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
    }

    default void tsFieldCreated(Property property, TSField tsField) {
    }

    default void tsMethodCreated(Method method, TSMethod tsMethod) {
    }

    default void tsParameterCreated(Parameter parameter, TSParameter tsParameter) {
    }
}
