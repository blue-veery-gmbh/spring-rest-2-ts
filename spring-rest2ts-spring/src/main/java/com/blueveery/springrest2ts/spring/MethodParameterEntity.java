package com.blueveery.springrest2ts.spring;

public class MethodParameterEntity {
    private String value;
    private String name;
    private boolean required;

    public String value() {
        return value;
    }

    public String name() {
        return name;
    }

    public boolean required() {
        return required;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
