package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Map;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class DefaultModuleConverter implements ModuleConverter {
    private int skipParentPackages = 0;

    public DefaultModuleConverter(int skipParentPackages) {
        this.skipParentPackages = skipParentPackages;
    }

    @Override
    public void convert(String packageName, Map<String, TSModule> moduleMap) {
        if(!moduleMap.containsKey(packageName)) {
            String[] packageNameComponents = packageName.split("\\.");
            StringBuilder moduleName = new StringBuilder();
            for (int i = skipParentPackages; i < packageNameComponents.length; i++) {
                moduleName.append(packageNameComponents[i]);
                if((i+1)<packageNameComponents.length){
                    moduleName.append('-');
                }
            }

            TSModule tsModule = new TSModule(moduleName.toString());
            moduleMap.put(packageName, tsModule);
        }
    }
}
