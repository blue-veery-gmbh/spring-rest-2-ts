package com.blueveery.springrest2ts.tests;

import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import java.util.SortedSet;

public interface ComplexElementFinder {
  default TSComplexElement findTSComplexElement(SortedSet<TSModule> tsModules, String name) {
    return (TSComplexElement) findScopedElement(tsModules.first(), name);
  }

  default TSComplexElement findTSComplexElement(TSModule tsModules, String name) {
    return (TSComplexElement) findScopedElement(tsModules, name);
  }

  default TSScopedElement findScopedElement(TSModule tsModules, String name) {
    return tsModules
        .getScopedTypesSet()
        .stream()
        .filter(t -> name.equals(t.getName()))
        .findFirst()
        .get();
  }
}