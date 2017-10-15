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
public class TSParameter extends TSElement implements IAnnotated {
    private TSType tsType;
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private List<Annotation> annotationList = new ArrayList<>();

    public TSParameter(String name, TSType tsType) {
        super(name);
        this.tsType = tsType;
    }

    public TSType getTsType() {
        return tsType;
    }

    public void setTsType(TSType tsType) {
        this.tsType = tsType;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.write(": ");
        writer.write(tsType.getName());
    }

    @Override
    public List<Annotation> getAnnotationList() {
        return annotationList;
    }
}
