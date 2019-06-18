package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.SpringREST2tsGenerator;
import com.blueveery.springrest2ts.converters.ModulePerJavaPackageConverter;
import com.blueveery.springrest2ts.examples.ctrls.OrderCtrl;
import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.OrderDTO;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.filters.AndFilterOperator;
import com.blueveery.springrest2ts.filters.BaseClassJavaTypeFilter;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class OrderSetupTest {

    private static final Path OUTPUT_DIR_PATH = Paths.get("./target/ts");

    private static SpringREST2tsGenerator tsGenerator;
    private static ModulePerJavaPackageConverter moduleConverter;

    @BeforeClass
    public static void setUp() {
        tsGenerator = new SpringREST2tsGenerator();
        tsGenerator.setModelClassesCondition(new BaseClassJavaTypeFilter(BaseDTO.class));
        tsGenerator.setRestClassesCondition(new BaseClassJavaTypeFilter(BaseCtrl.class));
        tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator(Paths.get("error-handling"), Paths.get("commons"), Paths.get("shared"))));

        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put(BaseDTO.class.getPackage().getName(), new TSModule("core", Paths.get("model"), false));
        packagesMap.put(OrderDTO.class.getPackage().getName(), new TSModule("model", Paths.get("model"), false));
        packagesMap.put(OrderCtrl.class.getPackage().getName(), new TSModule("services", Paths.get("services"), false));

        tsGenerator.getPackagesNames().addAll(packagesMap.keySet());
        moduleConverter = new ModulePerJavaPackageConverter(packagesMap);
    }

    @Test
    public void defaultSetup() throws IOException {
        tsGenerator.generate(moduleConverter, OUTPUT_DIR_PATH);
    }

    @Test
    public void complexRestClassCondition() throws IOException {
        OrFilterOperator annotationConditions = new OrFilterOperator(Arrays.asList(new HasAnnotationJavaTypeFilter(Controller.class), new HasAnnotationJavaTypeFilter(RestController.class)));
        tsGenerator.setRestClassesCondition(new AndFilterOperator(Arrays.asList(new BaseClassJavaTypeFilter(BaseCtrl.class), annotationConditions)));
        tsGenerator.generate(moduleConverter, OUTPUT_DIR_PATH);
    }
}
