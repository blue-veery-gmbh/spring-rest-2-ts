package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelClassesAbstractConverter extends ComplexTypeConverter{
    protected ObjectMapper defaultObjectMapper;
    private Map<String, ObjectMapper> objectMapperMap = new HashMap<>();

    public ModelClassesAbstractConverter(ImplementationGenerator implementationGenerator, ObjectMapper objectMapper) {
        super(implementationGenerator);
        this.defaultObjectMapper = objectMapper;
    }

    public ModelClassesAbstractConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(implementationGenerator, classNameMapper);
        this.defaultObjectMapper = objectMapper;
    }

    public Map<String, ObjectMapper> getObjectMapperMap() {
        return objectMapperMap;
    }

    protected ObjectMapper selectObjectMapper(Class javaClass) {
        String packageName = javaClass.getPackage().getName();
        do{
            ObjectMapper objectMapper = objectMapperMap.get(packageName);
            if (objectMapper != null) {
                return objectMapper;
            }
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
        }while (packageName.contains("."));
        return defaultObjectMapper;
    }
}
