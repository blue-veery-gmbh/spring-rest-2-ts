package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.INullableElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface NullableTypesStrategy {
    void setAsNullableType(Type elementType, Annotation[] declaredAnnotations, INullableElement tsElement);
}
