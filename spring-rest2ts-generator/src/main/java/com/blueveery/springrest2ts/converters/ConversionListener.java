package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSScopedType;

import java.lang.reflect.Method;

public interface ConversionListener {
    default void tsComplexTypeCreated(Class javaType, TSScopedType tsScopedType) {
    }

    default void tsFieldCreated(Property property, TSField tsField) {
    }

    default void tsMethodCreated(Method method, TSMethod tsMethod) {
    }
}
