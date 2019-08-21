package com.blueveery.springrest2ts;

public class SubstringClassNameMapper implements ClassNameMapper {
    private String from;
    private String to;

    public SubstringClassNameMapper(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String mapJavaClassNameToTs(String javaClassName) {
        return javaClassName.replace(from, to);
    }
}
