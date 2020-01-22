package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.examples.ctrls.spring.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.Named;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.filters.*;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ExtendedTsCodeGenerationsTest extends TsCodeGenerationsTest{

    @Test
    public void tsModuleCreatorConverter() throws IOException {
        TsModuleCreatorConverter moduleConverter = new TsModuleCreatorConverter(3);
        tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter);

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void configurableTsModulesConverter() throws IOException {
        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put("com.blueveery.springrest2ts.examples.model.core", new TSModule("core", Paths.get("app/sdk/model"), false));
        packagesMap.put("com.blueveery.springrest2ts.examples.model", new TSModule("model", Paths.get("app/sdk/model"), false));
        packagesMap.put("com.blueveery.springrest2ts.examples.model.enums", new TSModule("model-enums", Paths.get("app/sdk/enums"), false));
        TSModule servicesModule = new TSModule("services", Paths.get("app/sdk/services"), false);
        packagesMap.put("com.blueveery.springrest2ts.examples.ctrls.spring.core", servicesModule);
        packagesMap.put("com.blueveery.springrest2ts.examples.ctrls.spring", servicesModule);
        ConfigurableTsModulesConverter moduleConverter = new ConfigurableTsModulesConverter(packagesMap);
        tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter);

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void nullableTypesConfig() throws IOException {
        DefaultNullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();
        nullableTypesStrategy.setUsePrimitiveTypesWrappers(false);
        tsGenerator.setNullableTypesStrategy(nullableTypesStrategy);
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void swaggerAnnotations() throws IOException {
        restClassesConverter.getConversionListener().getConversionListenerSet().add(new SwaggerConversionListener());

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void jacksonAnnotationOnProductDTO() throws IOException {
        tsGenerator.setModelClassesCondition(new ContainsSubStringJavaTypeFilter("ProductDTO"));
        tsGenerator.setRestClassesCondition(new ContainsSubStringJavaTypeFilter("Ctrl"));

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void enumsToUnionsTest() throws IOException {
        tsGenerator.setModelClassesCondition(new ContainsSubStringJavaTypeFilter("DTO"));
        tsGenerator.setRestClassesCondition(new ContainsSubStringJavaTypeFilter("Ctrl"));
        tsGenerator.setEnumConverter(new JavaEnumToTsUnionConverter());
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void javaEnumsToTsEnumTest() throws IOException {
        tsGenerator.setModelClassesCondition(new ContainsSubStringJavaTypeFilter("DTO"));
        tsGenerator.setRestClassesCondition(new ContainsSubStringJavaTypeFilter("Ctrl"));
        tsGenerator.setEnumConverter(new JavaEnumToTsEnumConverter());

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }


    @Test
    public void classNameMappingTest() throws IOException {
        modelClassesConverter.setClassNameMapper(new SubstringClassNameMapper("DTO", ""));
        restClassesConverter.setClassNameMapper(new SubstringClassNameMapper("Ctrl", "Service"));

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void containsSubStringClassCondition() throws IOException {
        tsGenerator.setModelClassesCondition(new ContainsSubStringJavaTypeFilter("DTO"));
        tsGenerator.setRestClassesCondition(new ContainsSubStringJavaTypeFilter("Ctrl"));
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void regexClassCondition() throws IOException {
        tsGenerator.setModelClassesCondition(new RegexpJavaTypeFilter("\\w*DTO\\b"));
        tsGenerator.setRestClassesCondition(new RegexpJavaTypeFilter("\\w*Ctrl\\b"));

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }


    @Test
    public void complexRestClassCondition() throws IOException {
        OrFilterOperator annotationConditions = new OrFilterOperator(Arrays.asList(new HasAnnotationJavaTypeFilter(Controller.class), new HasAnnotationJavaTypeFilter(RestController.class)));
        tsGenerator.setRestClassesCondition(new AndFilterOperator(Arrays.asList(new ExtendsJavaTypeFilter(BaseCtrl.class), annotationConditions)));

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }

    @Test
    public void interfaceInModelClassesCondition() throws IOException {
        JavaTypeFilter namedInterfacesIncluded = new JavaTypeSetFilter(Collections.singleton(Named.class));
        tsGenerator.setModelClassesCondition(new OrFilterOperator(Arrays.asList(new ExtendsJavaTypeFilter(BaseDTO.class), namedInterfacesIncluded)));

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
