package com.blueveery.springrest2ts.converters;


import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Property {
    private String name;
    private Field field;
    private Method getter;
    private Method setter;
    private Class<?> declaringClass;

    public Property(String name) {
        this.name = name;
    }

    public Property(String name, Field field) {
        this.name = name;
        this.field = field;
        declaringClass = field.getDeclaringClass();
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
        declaringClass = getter.getDeclaringClass();
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
        declaringClass = setter.getDeclaringClass();
    }

    public boolean isReadOnly() {
        return field == null && setter == null;
    }

    public Type getGenericType() {
        if (field != null) {
            return field.getGenericType();
        }
        if (getter != null) {
            return getter.getGenericReturnType();
        }

        if (setter != null) {
            return setter.getParameterTypes()[0];
        }
        return null;
    }

    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return getFirstMember().getDeclaredAnnotation(annotationClass);
    }

    public Class<?> getDeclaringClass(){
        return declaringClass;
    }

    private AccessibleObject getFirstMember(){
        if (field != null) {
            return field;
        }
        if (getter != null) {
            return getter;
        }

        if (setter != null) {
            return setter;
        }
        return null;
    }
}
