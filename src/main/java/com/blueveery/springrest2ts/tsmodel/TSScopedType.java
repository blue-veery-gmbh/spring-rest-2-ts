package com.blueveery.springrest2ts.tsmodel;

/**
 * Created by tomek on 08.08.17.
 */
public abstract class TSScopedType extends TSType {
    protected TSModule module;

    protected TSScopedType(String name, TSModule module) {
        super(name);
        this.module = module;
    }

    public TSModule getModule() {
        return module;
    }
}
