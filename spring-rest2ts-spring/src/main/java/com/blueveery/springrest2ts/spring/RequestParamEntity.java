package com.blueveery.springrest2ts.spring;

import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;

public class RequestParamEntity extends MethodParameterEntity implements RequestParam {

    private String defaultValue;

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RequestParam.class;
    }
}
