package com.blueveery.springrest2ts.examples.test;


import com.blueveery.springrest2ts.converters.SpringDataRestConversionExtension;
import com.blueveery.springrest2ts.naming.SubstringClassNameMapper;
import org.junit.Test;

import java.io.IOException;


public class SpringDataPageableTest extends TsCodeGenerationsTest {

    @Test
    public void controllerWithPageableParam() throws IOException {
        modelClassesConverter.setClassNameMapper(new SubstringClassNameMapper("DTO", ""));
        restClassesConverter.setClassNameMapper(new SubstringClassNameMapper("Ctrl", "Service"));

        restClassesConverter.getConversionExtensionList().add(new SpringDataRestConversionExtension());

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
