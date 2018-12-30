package com.blueveery.springrest2ts.tsmodel;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszw on 30.07.2017.
 */
public abstract class TSComplexTypeMember extends TSElement implements IAnnotated, IDecorated{
    private TSComplexType owner;
    private TSType type;
    private List<TSDecorator> tsDecoratorList = new ArrayList<>();

    private List<Annotation> annotationList = new ArrayList<>();

    public TSComplexTypeMember(String name, TSComplexType owner, TSType type) {
        super(name);
        this.owner = owner;
        this.type = type;
        this.owner.addScopedTypeUsage(type);
    }

    public List<TSDecorator> getTsDecoratorList() {
        return tsDecoratorList;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public TSComplexType getOwner() {
        return owner;
    }

    public TSType getType() {
        return type;
    }

    public void setType(TSType type) {
        this.type = type;
    }
}
