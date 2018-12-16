package com.blueveery.springrest2ts;

import com.blueveery.bluehr.ctrls.ConsultantCtrl;
import com.blueveery.bluehr.model.Consultant;
import com.blueveery.core.ctrls.BaseCtrl;
import com.blueveery.core.model.BaseEntity;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tomaszw on 01.08.2017.
 */
public class SpringREST2tsGeneratorTest {

    @Test
    public void generationTest() throws IOException {
        SpringREST2tsGenerator rest2tsGenerator = new SpringREST2tsGenerator();
        Set<String> packagesNames = new HashSet<>();
        Set<Class> modelClassNamesConditions = new HashSet<>();
        Set<Class> restClassNamesConditions = new HashSet<>();

        packagesNames.add(BaseEntity.class.getPackage().getName());
        packagesNames.add(Consultant.class.getPackage().getName());
        packagesNames.add(ConsultantCtrl.class.getPackage().getName());
        modelClassNamesConditions.add(BaseEntity.class);
        restClassNamesConditions.add(BaseCtrl.class);
        File outputDir = new File("./target/generated-sources/ts");

        rest2tsGenerator.setGenerationContext(new GenerationContext(new Angular4ImplementationGenerator()));

        rest2tsGenerator.generate(packagesNames, modelClassNamesConditions, restClassNamesConditions, new HashMap<>(), outputDir);
    }
}
