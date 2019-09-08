package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class TsModuleCreatorConverter implements JavaPackageToTsModuleConverter {
    private int numberOfSubPackages;
    private Map<String, TSModule> packagesMap = new HashMap<>();
    private SortedSet<TSModule> tsModuleSortedSet = new TreeSet<>();

    public TsModuleCreatorConverter(int numberOfSubPackages) {
        this.numberOfSubPackages = numberOfSubPackages;
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
            String[] subPackages = packageName.split("\\.");
            StringBuilder moduleName = new StringBuilder();
            for(int i=Math.max(subPackages.length-numberOfSubPackages, 0); i<subPackages.length; i++) {
                moduleName.append(subPackages[i]);
                if (i+1<subPackages.length) {
                    moduleName.append("-");
                }
            }
            tsModule = new TSModule(moduleName.toString(), Paths.get("."), false);
            packagesMap.put(packageName, tsModule);
            tsModuleSortedSet.add(tsModule);
        }
        return tsModule;
    }
}
