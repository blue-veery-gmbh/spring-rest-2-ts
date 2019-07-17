package com.blueveery.springrest2ts.examples.setups;

import com.blueveery.core.model.BaseEntity;
import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.SpringREST2tsGenerator;
import com.blueveery.springrest2ts.converters.ModulePerJavaPackageConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filters.BaseClassJavaTypeFilter;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

public class BluehrSetup {
    public static void main(String[] args) throws IOException {
        SpringREST2tsGenerator springREST2tsGenerator = new SpringREST2tsGenerator();
        springREST2tsGenerator.setModelClassesCondition(new BaseClassJavaTypeFilter(BaseEntity.class));
        springREST2tsGenerator.setRestClassesCondition(new HasAnnotationJavaTypeFilter(Component.class));
        springREST2tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator(Paths.get("shared"))));

        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put("com.blueveery.core.model", new TSModule("core", Paths.get("bluehr"), false));
        packagesMap.put("com.blueveery.bluehr.model", new TSModule("model", Paths.get("bluehr"), false));
        packagesMap.put("com.blueveery.bluehr.ctrls", new TSModule("controllers", Paths.get("bluehr"), false));
        ModulePerJavaPackageConverter moduleConverter = new ModulePerJavaPackageConverter(packagesMap);

        springREST2tsGenerator.getCustomTypeMapping().put(UUID.class, TypeMapper.tsString);

        springREST2tsGenerator.getPackagesNames().addAll(packagesMap.keySet());
        springREST2tsGenerator.generate(moduleConverter, Paths.get("./target/ts"));
    }
}
