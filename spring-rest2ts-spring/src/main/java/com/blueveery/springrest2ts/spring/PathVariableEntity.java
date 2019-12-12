package com.blueveery.springrest2ts.spring;

import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;

public class PathVariableEntity extends MethodParameterEntity implements PathVariable {


    @Override
    public Class<? extends Annotation> annotationType() {
        return PathVariable.class;
    }
}
