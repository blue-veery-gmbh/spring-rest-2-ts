package com.blueveery.springrest2ts.spring;

public class MethodParameterEntity {
    private String name = "";
    private boolean required = true;

    public String value() {
        return name;
    }

    public String name() {
        return name;
    }

    public boolean required() {
        return required;
    }

    public void setValue(String value) {
        this.name = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
