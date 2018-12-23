package com.blueveery.springrest2ts;

import com.blueveery.bluehr.ctrls.ConsultantCtrl;
import com.blueveery.bluehr.model.Consultant;
import com.blueveery.core.ctrls.BaseCtrl;
import com.blueveery.core.model.BaseEntity;
import com.blueveery.springrest2ts.converters.ModulePerJavaPackageConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filters.BaseClassJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSSimpleType;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by tomaszw on 01.08.2017.
 */
public class SpringREST2tsGeneratorTest {


    public static void main(String[] args) throws IOException {
        SpringREST2tsGenerator rest2tsGenerator = new SpringREST2tsGenerator();


        HashMap<String, TSModule> packagesMap = new HashMap<>();
        packagesMap.put(BaseEntity.class.getPackage().getName(), new TSModule("core-model", Paths.get("services"), false));
        packagesMap.put(Consultant.class.getPackage().getName(), new TSModule("bluehr-model", Paths.get("services"), false));
        packagesMap.put(ConsultantCtrl.class.getPackage().getName(), new TSModule("bluehr-services", Paths.get("services"), false));

        packagesMap.keySet().forEach(packageName -> rest2tsGenerator.getPackagesNames().add(packageName));

        rest2tsGenerator.setModelClassesCondition(new BaseClassJavaTypeFilter(BaseEntity.class));
        rest2tsGenerator.setRestClassesCondition(new BaseClassJavaTypeFilter(BaseCtrl.class));

        rest2tsGenerator.getCustomTypeMapping().put(UUID.class, TypeMapper.tsString);

        Path outputDir = Paths.get("./target/generated-sources/ts");

        rest2tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator()));

        rest2tsGenerator.generate(new ModulePerJavaPackageConverter(packagesMap), outputDir);
    }
}
