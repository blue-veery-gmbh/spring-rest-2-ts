package com.blueveery.springrest2ts.examples.setups;

import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.SpringREST2tsGenerator;
import com.blueveery.springrest2ts.converters.ModulePerJavaPackageConverter;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.examples.model.orders.OrderDTO;
import com.blueveery.springrest2ts.filters.BaseClassJavaTypeFilter;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class OrderSetupOne {
    public static void main(String[] args) throws IOException {
        SpringREST2tsGenerator springREST2tsGenerator = new SpringREST2tsGenerator();
        springREST2tsGenerator.setModelClassesCondition(new BaseClassJavaTypeFilter(BaseDTO.class));
        springREST2tsGenerator.setRestClassesCondition(new HasAnnotationJavaTypeFilter(Component.class));
        springREST2tsGenerator.setGenerationContext(new GenerationContext(new EmptyImplementationGenerator()));

        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put(BaseDTO.class.getPackage().getName(), new TSModule("core", Paths.get("model"), false));
        packagesMap.put(OrderDTO.class.getPackage().getName(), new TSModule("orders", Paths.get("model"), false));
        ModulePerJavaPackageConverter moduleConverter = new ModulePerJavaPackageConverter(packagesMap);

        springREST2tsGenerator.getPackagesNames().addAll(packagesMap.keySet());
        springREST2tsGenerator.generate(moduleConverter, Paths.get("./target/ts"));
    }
}
