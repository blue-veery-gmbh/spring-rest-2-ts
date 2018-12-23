package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class DefaultModuleConverter implements ModuleConverter {
    private int skipParentPackages = 0;
    private Map<String, String> packagesMap = new HashMap<>();

    public DefaultModuleConverter(int skipParentPackages) {
        this.skipParentPackages = skipParentPackages;
    }

    public Map<String, String> getPackagesMap() {
        return packagesMap;
    }

    @Override
    public void convert(String packageName, Map<String, TSModule> moduleMap) {
        String mappedPackageName = packageName;
        if(packagesMap.containsKey(packageName)){
            mappedPackageName = packagesMap.get(packageName);
        }
        if(!moduleMap.containsKey(mappedPackageName)) {
            String[] packageNameComponents = mappedPackageName.split("\\.");
            StringBuilder moduleName = new StringBuilder();
            for (int i = skipParentPackages; i < packageNameComponents.length; i++) {
                moduleName.append(packageNameComponents[i]);
                if((i+1)<packageNameComponents.length){
                    moduleName.append('-');
                }
            }

            TSModule tsModule = new TSModule(moduleName.toString(), Paths.get("services"), false);
            moduleMap.put(mappedPackageName, tsModule);
        }
        if(mappedPackageName!=packageName){
            moduleMap.put(packageName, moduleMap.get(mappedPackageName));
        }
    }
}
