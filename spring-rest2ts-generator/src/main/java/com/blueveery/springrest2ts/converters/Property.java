package com.blueveery.springrest2ts.converters;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Property {
    private String name;
    private Field field;
    private Method getter;
    private Method setter;

    public Property(String name) {
        this.name = name;
    }

    public Property(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public boolean isReadOnly() {
        return field == null && getter == null;
    }

    public Type getGenericType() {
        return null;
    }

    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {

        return null;
    }
    public Class<?> getDeclaringClass(){

    }
}
