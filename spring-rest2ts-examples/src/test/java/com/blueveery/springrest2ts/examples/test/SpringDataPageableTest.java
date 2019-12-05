package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.converters.SpringDataRestConversionExtension;
import org.junit.Test;


import java.io.IOException;


public class SpringDataPageableTest extends TsCodeGenerationsTest {

    @Test
    public void controllerWithPageableParam() throws IOException {
        restClassesConverter.getConversionExtensionList().add(new SpringDataRestConversionExtension());

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
