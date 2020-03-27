package com.blueveery.springrest2ts.converters;


import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Property implements Comparable<Property>{
    private String name;
    private int index = 0;
    private Field field;
    private Method getter;
    private Method setter;
    private boolean isIgnored;
    private Class<?> declaringClass;

    public Property(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Property(String name, int index, Field field) {
        this.name = name;
        this.index = index;
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

    public boolean isIgnored() {
        return isIgnored;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public boolean isReadOnly() {
        return field == null && setter == null;
    }

    public Type getGetterType() {
        if (getter != null) {
            return getter.getGenericReturnType();
        }
        if (field != null) {
            return field.getGenericType();
        }

        return null;
    }


    public Type getSetterType() {
        if (setter != null) {
            return setter.getGenericParameterTypes()[0];
        }
        if (field != null) {
            return field.getGenericType();
        }
        return null;
    }

    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        A requiredAnnotation = null;
        if (getter != null) {
            requiredAnnotation = getter.getDeclaredAnnotation(annotationClass);
            if (requiredAnnotation != null) {
                return requiredAnnotation;
            }
        }

        if (setter != null) {
            requiredAnnotation = setter.getDeclaredAnnotation(annotationClass);
            if (requiredAnnotation != null) {
                return requiredAnnotation;
            }
            requiredAnnotation = setter.getParameters()[0].getDeclaredAnnotation(annotationClass);
            if (requiredAnnotation != null) {
                return requiredAnnotation;
            }
        }
        if (field != null) {
            return field.getDeclaredAnnotation(annotationClass);
        }
        return null;
    }


    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> annotationList = new ArrayList<>();
        if (getter != null) {
            for (Annotation annotation : getter.getDeclaredAnnotations()) {
                annotationList.add(annotation);
            }
        }

        if (setter != null) {
            for (Annotation annotation : setter.getDeclaredAnnotations()) {
                annotationList.add(annotation);
            }
            for (Annotation annotation : setter.getParameters()[0].getDeclaredAnnotations()) {
                annotationList.add(annotation);
            }
        }
        if (field != null) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                annotationList.add(annotation);
            }
        }
        return annotationList.toArray(new Annotation[annotationList.size()]);
    }

    public Class<?> getDeclaringClass(){
        return declaringClass;
    }

    @Override
    public int compareTo(Property otherProperty) {
        return index - otherProperty.index;
    }
}
