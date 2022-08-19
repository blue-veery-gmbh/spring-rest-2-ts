package com.blueveery.springrest2ts.converters;


import static org.assertj.core.api.Assertions.assertThat;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.ctrls.ProductController;
import com.blueveery.springrest2ts.converters.enums.ProductType;
import com.blueveery.springrest2ts.converters.enums.SingleResult;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tests.ComplexElementFinder;
import com.blueveery.springrest2ts.tsmodel.TSElement;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import org.junit.Before;
import org.junit.Test;

public class SpringRestToTsConverterTest implements ComplexElementFinder {
  protected Rest2tsGenerator tsGenerator;
  protected JacksonObjectMapper objectMapper;
  protected Set<String> javaPackageSet;
  protected ModelClassesAbstractConverter modelClassesConverter;

  protected void printTSElement(TSElement tsClass) throws IOException {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    tsClass.write(writer);
    writer.flush();
  }

  @Before
  public void setUp() {
    tsGenerator = new Rest2tsGenerator();
    tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(ProductType.class, SingleResult.class));
    tsGenerator.setRestClassesCondition(new JavaTypeSetFilter(ProductController.class));
    objectMapper = new JacksonObjectMapper();
    modelClassesConverter = new ModelClassesToTsInterfacesConverter(objectMapper);
    tsGenerator.setModelClassesConverter(modelClassesConverter);
    tsGenerator.setRestClassesConverter(new SpringRestToTsConverter(new Angular4ImplementationGenerator()));
    javaPackageSet = new HashSet<>();
    javaPackageSet.add("com.blueveery.springrest2ts.converters.enums");
    javaPackageSet.add("com.blueveery.springrest2ts.converters.ctrls");
  }

  @Test
  public void endpointReturnTypeShouldBeImported() throws IOException {
    SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
    TSModule tsEnumModule = tsModules
        .stream()
        .filter(m -> m.getName().contains("enums"))
        .findFirst()
        .get();
    TSModule tsCtrlsModule = tsModules
        .stream()
        .filter(m -> m.getName().contains("ctrls"))
        .findFirst()
        .get();
    TSScopedElement tsProductType = findScopedElement(
        tsEnumModule, ProductType.class.getSimpleName()
    );

    TSScopedElement tsSingleResult = findScopedElement(
        tsEnumModule, SingleResult.class.getSimpleName()
    );
    assertThat(tsCtrlsModule.getImportMap().get(tsEnumModule)).isNotNull();
    assertThat(tsCtrlsModule.getImportMap().get(tsEnumModule).getWhat()).contains(tsSingleResult);
    assertThat(tsCtrlsModule.getImportMap().get(tsEnumModule).getWhat()).contains(tsProductType);

    printTSElement(tsCtrlsModule);
  }

}