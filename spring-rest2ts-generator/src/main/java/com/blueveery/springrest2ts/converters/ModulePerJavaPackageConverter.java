package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModulePerJavaPackageConverter implements ModuleConverter {
    private Map<String, TSModule> packagesMap;
    private SortedSet<TSModule> tsModuleSortedSet = new TreeSet<>();

    public ModulePerJavaPackageConverter(Map<String, TSModule> packagesMap) {
        this.packagesMap = packagesMap;
        tsModuleSortedSet.addAll(packagesMap.values());
    }

    @Override
    public void mapJavaTypeToTsModule(Class javaType) {
        getTsModule(javaType);
    }

    @Override
    public SortedSet<TSModule> getTsModules() {
        return tsModuleSortedSet;
    }

    @Override
    public TSModule getTsModule(Class javaType) {
        String packageName = javaType.getPackage().getName();
        TSModule tsModule = packagesMap.get(packageName);
        if (tsModule == null) {
            throw new IllegalStateException("missing mapping for package :" + packageName);
        }
        return tsModule;
    }
}
