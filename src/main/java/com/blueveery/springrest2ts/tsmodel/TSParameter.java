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
public class TSParameter extends TSElement implements IAnnotated, IDecorated {
    private TSType tsType;
    private String defaultValue; // mock default value
    private boolean optional;
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();
    private List<Annotation> annotationList = new ArrayList<>();

    public TSParameter(String name, TSType tsType) {
        super(name);
        this.tsType = tsType;
        this.optional = false;
        this.defaultValue = null;
    }

    public TSParameter(String name, TSType tsType, String defaultValue) {
        super(name);
        this.tsType = tsType;
        this.optional = true;
        this.defaultValue = defaultValue;
    }

    public TSParameter(String name, TSType tsType, boolean optional, String defaultValue) {
        super(name);
        this.tsType = tsType;
        this.optional = optional;
        this.defaultValue = defaultValue;
    }

    public TSType getTsType() {
        return tsType;
    }

    public void setTsType(TSType tsType) {
        this.tsType = tsType;
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
        if (tsType instanceof TSArrowFuncType) {
            tsType.write(generationContext, writer);
        } else {
            writer.write(tsType.getName());
        }
        if (defaultValue != null) {
            writer.write(" = ");
            boolean isStringType = TypeMapper.tsString.equals(tsType);
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
