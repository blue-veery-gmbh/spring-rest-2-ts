package com.blueveery.springrest2ts;

import com.blueveery.bluehr.ctrls.ConsultantCtrl;
import com.blueveery.bluehr.model.Consultant;
import com.blueveery.core.ctrls.BaseCtrl;
import com.blueveery.core.model.BaseEntity;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by tomaszw on 01.08.2017.
 */
public class SpringREST2tsGeneratorTest {


    public static void main(String[] args) throws IOException {
        SpringREST2tsGenerator rest2tsGenerator = new SpringREST2tsGenerator();


        rest2tsGenerator.getPackagesNames().add(BaseEntity.class.getPackage().getName());
        rest2tsGenerator.getPackagesNames().add(Consultant.class.getPackage().getName());
        rest2tsGenerator.getPackagesNames().add(ConsultantCtrl.class.getPackage().getName());

        rest2tsGenerator.getModelClassesConditions().add(BaseEntity.class);
        rest2tsGenerator.getRestClassesConditions().add(BaseCtrl.class);

        rest2tsGenerator.getCustomTypeMapping().put(UUID.class, "string");

        File outputDir = new File("./target/generated-sources/ts");

        rest2tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator()));

        rest2tsGenerator.generate(outputDir);
    }
}
