package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

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
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private List<Annotation> annotationList = new ArrayList<>();

    private String defaultValue; // mock default value

    public TSParameter(String name, TSType tsType) {
        super(name);
        this.tsType = tsType;
        this.defaultValue = "";
    }

    public TSParameter(String name, TSType tsType, String defaultValue) {
        super(name);
        this.tsType = tsType;
        this.defaultValue = defaultValue;
    }

    public TSType getTsType() {
        return tsType;
    }

    public void setTsType(TSType tsType) {
        this.tsType = tsType;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writeDecorators(generationContext, writer, tsDecoratorList);
        writer.write(getName());
        writer.write(": ");
        if (tsType instanceof TSArrowFuncType) {
            tsType.write(generationContext, writer);
        } else {
            writer.write(tsType.getName());
        }
        if (defaultValue.compareTo("") != 0) {
            writer.write(" = ");
            writer.write(defaultValue);
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
