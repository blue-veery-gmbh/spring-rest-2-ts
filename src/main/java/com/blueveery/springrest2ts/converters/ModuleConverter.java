package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Set;
import java.util.SortedSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public interface ModuleConverter {
    void mapJavaTypeToTsModule(Class packageName);
    TSModule getTsModule(Class javaClass);
    SortedSet<TSModule> getTsModules();
}
