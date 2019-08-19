package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.INullableElement;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    public abstract boolean preConverted(ModuleConverter moduleConverter, Class javaClass);
    public abstract void convert(Class javaClass);

    protected final void setAsNullableType(Type elementType, Annotation[] declaredAnnotations, INullableElement tsElement) {
        if(!tsElement.isNullable()){
            if (elementType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) elementType;
                if(Optional.class == parameterizedType.getRawType()){
                    tsElement.setNullable(true);
                    return;
                }
            }

            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof Nullable) {
                    tsElement.setNullable(true);
                    return;
                }
            }

            if(elementType instanceof Class) {
                Class elementClass = (Class) elementType;
                if (Number.class.isAssignableFrom(elementClass)) {
                    tsElement.setNullable(true);
                    return;
                }
            }

            if(Boolean.class == elementType){
                tsElement.setNullable(true);
                return;
            }
        }
    }
}
