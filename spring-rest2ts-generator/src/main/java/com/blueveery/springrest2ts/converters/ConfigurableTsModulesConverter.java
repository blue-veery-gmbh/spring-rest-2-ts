package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ConfigurableTsModulesConverter implements JavaPackageToTsModuleConverter {
    private TsModuleCreatorConverter tsModuleCreatorConverter;
    private Map<String, TSModule> packagesMap;
    private SortedSet<TSModule> tsModuleSortedSet = new TreeSet<>();

    public ConfigurableTsModulesConverter(Map<String, TSModule> packagesMap) {
        this.packagesMap = packagesMap;
        tsModuleSortedSet.addAll(packagesMap.values());
    }

    public ConfigurableTsModulesConverter(Map<String, TSModule> packagesMap, TsModuleCreatorConverter tsModuleCreatorConverter) {
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
            if (tsModuleCreatorConverter != null) {
                tsModule = tsModuleCreatorConverter.getTsModule(javaType);
                packagesMap.put(packageName, tsModule);
                tsModuleSortedSet.add(tsModule);
                return tsModule;
            }
            throw new IllegalStateException("missing mapping for package :" + packageName);
        }

        return tsModule;
    }
}
