package com.blueveery.springrest2ts.spring;

import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;

public class RequestBodyEntity implements RequestBody {
    private boolean required = true;

    @Override
    public boolean required() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RequestBody.class;
    }
}
