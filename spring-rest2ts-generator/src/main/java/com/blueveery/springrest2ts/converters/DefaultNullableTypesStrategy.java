package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.INullableElement;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class DefaultNullableTypesStrategy implements NullableTypesStrategy {
    private boolean useOptional = true;
    private boolean useNullableAnnotation = true;
    private boolean usePrimitiveTypesWrappers = true;

    public boolean isUseOptional() {
        return useOptional;
    }

    public void setUseOptional(boolean useOptional) {
        this.useOptional = useOptional;
    }

    public boolean isUseNullableAnnotation() {
        return useNullableAnnotation;
    }

    public void setUseNullableAnnotation(boolean useNullableAnnotation) {
        this.useNullableAnnotation = useNullableAnnotation;
    }

    public boolean isUsePrimitiveTypesWrappers() {
        return usePrimitiveTypesWrappers;
    }

    public void setUsePrimitiveTypesWrappers(boolean usePrimitiveTypesWrappers) {
        this.usePrimitiveTypesWrappers = usePrimitiveTypesWrappers;
    }

    @Override
    public void setAsNullableType(Type elementType, Annotation[] declaredAnnotations, INullableElement tsElement) {
        if(!tsElement.isNullable()){

            if (useOptional) {
                if (elementType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) elementType;
                    if(Optional.class == parameterizedType.getRawType()){
                        tsElement.setNullable(true);
                        return;
                    }
                }
            }

            if (useNullableAnnotation) {
                for (Annotation annotation : declaredAnnotations) {
                    if (annotation instanceof Nullable ) {
                        tsElement.setNullable(true);
                        return;
                    }
                }
            }

            if (usePrimitiveTypesWrappers) {
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
}
