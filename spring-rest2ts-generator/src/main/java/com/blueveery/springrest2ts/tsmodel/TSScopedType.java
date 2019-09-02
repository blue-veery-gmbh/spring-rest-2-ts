package com.blueveery.springrest2ts.tsmodel;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 08.08.17.
 */
public abstract class TSScopedType extends TSType implements ICommentedElement, IAnnotated{
    protected TSModule module;
    protected TSComment tsComment = new TSComment("ComplexTypeComment");
    private List<Annotation> annotationList = new ArrayList<>();

    protected TSScopedType(String name, TSModule module) {
        super(name);
        this.module = module;
    }

    public TSModule getModule() {
        return module;
    }

    @Override
    public TSComment getTsComment() {
        return tsComment;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }
}
