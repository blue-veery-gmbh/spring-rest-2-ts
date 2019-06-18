package com.blueveery.springrest2ts.examples.setups;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.SpringREST2tsGenerator;
import com.blueveery.springrest2ts.converters.ModulePerJavaPackageConverter;
import com.blueveery.springrest2ts.examples.ctrls.OrderCtrl;
import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.OrderDTO;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.filters.BaseClassJavaTypeFilter;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.implgens.Angular4JsonScopeImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class OrderSetupOne {
    public static void main(String[] args) throws IOException {
        SpringREST2tsGenerator springREST2tsGenerator = new SpringREST2tsGenerator();
        springREST2tsGenerator.setModelClassesCondition(new BaseClassJavaTypeFilter(BaseDTO.class));
        springREST2tsGenerator.setRestClassesCondition(new BaseClassJavaTypeFilter(BaseCtrl.class));
        springREST2tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator(Paths.get("error-handling"), Paths.get("commons"), Paths.get("shared"))));

        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put(BaseDTO.class.getPackage().getName(), new TSModule("core", Paths.get("model"), false));
        packagesMap.put(OrderDTO.class.getPackage().getName(), new TSModule("model", Paths.get("model"), false));
        packagesMap.put(OrderCtrl.class.getPackage().getName(), new TSModule("services", Paths.get("services"), false));
        ModulePerJavaPackageConverter moduleConverter = new ModulePerJavaPackageConverter(packagesMap);

        springREST2tsGenerator.getPackagesNames().addAll(packagesMap.keySet());
        springREST2tsGenerator.generate(moduleConverter, Paths.get("./target/ts"));
    }
}
