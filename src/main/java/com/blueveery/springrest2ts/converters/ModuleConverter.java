package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.util.Map;

/**
 * Created by tomaszw on 03.08.2017.
 */
public interface ModuleConverter {
    void convert(String packageName, Map<String, TSModule> modulesSet);
}
