package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSParameter extends TSElement implements INullableElement, IAnnotated, IDecorated {
    private TSType type;
    private String defaultValue; // mock default value
    private boolean optional;
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();
    private List<Annotation> annotationList = new ArrayList<>();
    private final TSMethod tsMethod;
    private ImplementationGenerator implementationGenerator;

    public TSParameter(String name, TSType type, TSMethod tsMethod, ImplementationGenerator implementationGenerator) {
        super(name);
        this.type = type;
        this.tsMethod = tsMethod;
        this.implementationGenerator = implementationGenerator;
        this.defaultValue = null;
        this.optional = false;
    }

    public TSParameter(String name, TSType type, TSMethod tsMethod, ImplementationGenerator implementationGenerator, String defaultValue) {
        this(name, type, tsMethod, implementationGenerator);
        this.defaultValue = defaultValue;
        this.optional = true;
    }

    public TSMethod getTsMethod() {
        return tsMethod;
    }

    @Override
    public TSType getType() {
        return type;
    }

    @Override
    public void setType(TSType type) {
        this.type = type;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writeDecorators(writer, tsDecoratorList);
        writer.write(getName());
        if (optional && defaultValue == null) {
            writer.write("?");
        }
        writer.write(": ");
        if (type instanceof TSArrowFuncType) {
            type.write(writer);
        } else {
            writer.write(type.getName());
        }
        if (defaultValue != null) {
            writer.write(" = ");
            boolean isStringType = TypeMapper.tsString.equals(type);
            if(isStringType){
                writer.write("\"");
            }
            writer.write(defaultValue);
            if(isStringType){
                writer.write("\"");
            }
        }
    }

    @Override
    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    @Override
    public List<TSDecorator> getTsDecoratorList() {
        return tsDecoratorList;
    }
}
