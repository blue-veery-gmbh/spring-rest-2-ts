package com.blueveery.springrest2ts;

public class NoChangeClassNameMapper implements ClassNameMapper {
    @Override
    public String mapJavaClassNameToTs(String javaClassName) {
        return javaClassName;
    }
}
