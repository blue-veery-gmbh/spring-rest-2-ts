package com.blueveery.springrest2ts.tests;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import java.util.SortedSet;

public interface ComplexElementFinder {
  default TSComplexElement findTSComplexElement(SortedSet<TSModule> tsModules, String name) {
    return (TSComplexElement) tsModules.first().
                                       getScopedTypesSet()
                                       .stream()
                                       .filter(t -> name.equals(t.getName()))
                                       .findFirst().get();
  }

  default TSComplexElement findTSComplexElement(TSModule tsModules, String name) {
    return (TSComplexElement) tsModules
        .getScopedTypesSet()
        .stream()
        .filter(t -> name.equals(t.getName()))
        .findFirst()
        .get();
  }
}