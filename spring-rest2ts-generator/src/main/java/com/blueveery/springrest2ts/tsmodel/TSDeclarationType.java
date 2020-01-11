package com.blueveery.springrest2ts.tsmodel;

public enum TSDeclarationType {
    VAR("var"), LET("let"), CONST("const");

    private String name;

    TSDeclarationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
