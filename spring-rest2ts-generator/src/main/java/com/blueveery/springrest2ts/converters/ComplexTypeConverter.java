package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.INullableElement;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    public abstract boolean preConverted(ModuleConverter moduleConverter, Class javaClass);
    public abstract void convert(Class javaClass);

    protected final void setAsNullableType(Class elementType, Annotation[] declaredAnnotations, INullableElement tsElement) {
        if(!tsElement.isNullable()){
            if(Optional.class == elementType){
                tsElement.setNullable(true);
                return;
            }

            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof Nullable) {
                    tsElement.setNullable(true);
                    return;
                }
            }

            if(Number.class.isAssignableFrom(elementType)){
                tsElement.setNullable(true);
                return;
            }

            if(Boolean.class == elementType){
                tsElement.setNullable(true);
                return;
            }
        }
    }
}
