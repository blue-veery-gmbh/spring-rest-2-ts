package com.blueveery.springrest2ts.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;

public class RequestMappingEntity implements RequestMapping {

    private String name = "";
    private RequestMethod[] method = {};
    private String[] produces = {};
    private String[] consumes = {};
    private String[] headers = {};
    private String[] path = {};
    private String[] value = {};
    private String[] params = {};

    public void setName(String name) {
        this.name = name;
    }

    public void setMethod(RequestMethod ...method) {
        this.method = method;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String[] value() {
        return value;
    }

    @Override
    public String[] path() {
        return path;
    }

    @Override
    public RequestMethod[] method() {
        return method;
    }

    @Override
    public String[] params() {
        return params;
    }

    @Override
    public String[] headers() {
        return headers;
    }

    @Override
    public String[] consumes() {
        return consumes;
    }

    @Override
    public String[] produces() {
        return produces;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RequestMapping.class;
    }
}
