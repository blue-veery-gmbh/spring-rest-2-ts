package com.blueveery.springrest2ts.spring;

public class MethodParameterEntity {
    private String name = "";
    private String value = "";
    private boolean required = true;

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
