package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.converters.TypeMapper;

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

    public TSParameter(String name, TSType type) {
        super(name);
        this.type = type;
        this.optional = false;
        this.defaultValue = null;
    }

    public TSParameter(String name, TSType type, String defaultValue) {
        super(name);
        this.type = type;
        this.optional = true;
        this.defaultValue = defaultValue;
    }

    public TSParameter(String name, TSType type, boolean optional, String defaultValue) {
        super(name);
        this.type = type;
        this.optional = optional;
        this.defaultValue = defaultValue;
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
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writeDecorators(generationContext, writer, tsDecoratorList);
        writer.write(getName());
        if (optional) {
            writer.write("?");
        }
        writer.write(": ");
        if (type instanceof TSArrowFuncType) {
            type.write(generationContext, writer);
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
