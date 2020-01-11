package com.blueveery.springrest2ts.tsmodel;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszw on 30.07.2017.
 */
public abstract class TSComplexTypeMember extends TSElement implements INullableElement, ICommentedElement, IAnnotated, IDecorated{
    protected TSComment tsComment = new TSComment("ComplexTypeMemberComment");
    private TSComplexElement owner;
    private TSType type;
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private List<Annotation> annotationList = new ArrayList<>();

    public TSComplexTypeMember(String name, TSComplexElement owner, TSType type) {
        super(name);
        this.owner = owner;
        this.type = type;
        this.owner.addScopedTypeUsage(type);
    }

    @Override
    public TSComment getTsComment() {
        return tsComment;
    }

    public List<TSDecorator> getTsDecoratorList() {
        return tsDecoratorList;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public TSComplexElement getOwner() {
        return owner;
    }

    @Override
    public TSType getType() {
        return type;
    }

    @Override
    public void setType(TSType type) {
        this.type = type;
    }
}
