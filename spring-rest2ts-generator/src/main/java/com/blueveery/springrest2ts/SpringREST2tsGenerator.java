package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class SpringREST2tsGenerator {

    static Logger logger = LoggerFactory.getLogger("gen-logger");
    private Map<Class, TSType> customTypeMapping = new HashMap<>();

    private JavaTypeFilter modelClassesCondition;
    private JavaTypeFilter restClassesCondition;
    private ModuleConverter moduleConverter = new TsModuleCreatorConverter(2);
    private ComplexTypeConverter enumConverter = new JavaEnumToTsEnumConverter();;
    private ComplexTypeConverter modelClassesConverter;
    private ComplexTypeConverter restClassesConverter;

    public Map<Class, TSType> getCustomTypeMapping() {
        return customTypeMapping;
    }

    public void setModelClassesCondition(JavaTypeFilter modelClassesCondition) {
        this.modelClassesCondition = modelClassesCondition;
    }

    public void setRestClassesCondition(JavaTypeFilter restClassesCondition) {
        this.restClassesCondition = restClassesCondition;
    }

    public void setModuleConverter(ModuleConverter moduleConverter) {
        this.moduleConverter = moduleConverter;
    }

    public void setEnumConverter(ComplexTypeConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    public void setModelClassesConverter(ComplexTypeConverter modelClassesConverter) {
        this.modelClassesConverter = modelClassesConverter;
    }

    public void setRestClassesConverter(ComplexTypeConverter restClassesConverter) {
        this.restClassesConverter = restClassesConverter;
    }

    public SortedSet<TSModule> generate(Set<String> packagesNames, Path outputDir) throws IOException {
        Set<Class> modelClasses = new HashSet<>();
        Set<Class> restClasses = new HashSet<>();
        Set<Class> enumClasses = new HashSet<>();

        logger.info("Scanning model classes");
        scanPackages(packagesNames, modelClassesCondition, modelClasses, enumClasses, logger);
        logger.info("Scanning rest controllers classes");
        scanPackages(packagesNames, restClassesCondition, restClasses, enumClasses, logger);


        registerCustomTypesMapping(customTypeMapping);

        exploreRestClasses(restClasses, modelClassesCondition, modelClasses);
        exploreModelClasses(modelClasses, restClassesCondition);

        convertModules(enumClasses, moduleConverter);
        convertModules(modelClasses, moduleConverter);
        convertModules(restClasses, moduleConverter);

        convertTypes(enumClasses, moduleConverter, enumConverter);
        if (!modelClasses.isEmpty()) {
            if (modelClassesConverter == null) {
                throw new IllegalStateException("Model classes converter is not set");
            }
            convertTypes(modelClasses, moduleConverter, modelClassesConverter);
        }

        if (!restClasses.isEmpty()) {
            if (restClassesConverter == null) {
                throw new IllegalStateException("Rest classes converter is not set");
            }
            convertTypes(restClasses, moduleConverter, restClassesConverter);
        }


        writeTSModules(moduleConverter.getTsModules(), outputDir, logger);

        return moduleConverter.getTsModules();
    }

    private void registerCustomTypesMapping(Map<Class, TSType> customTypeMapping) {
        for (Class nextJavaType : customTypeMapping.keySet()) {
            TSType tsType = customTypeMapping.get(nextJavaType);
            TypeMapper.registerTsType(nextJavaType, tsType);
        }
    }

    private void writeTSModules(SortedSet<TSModule> tsModuleSortedSet, Path outputDir, Logger logger) throws IOException {
        for (TSModule tsModule : tsModuleSortedSet) {
            tsModule.writeModule(outputDir, logger);
        }
    }

    private void convertModules(Set<Class> javaClasses, ModuleConverter moduleConverter) {
        for (Class javaType : javaClasses) {
            moduleConverter.mapJavaTypeToTsModule(javaType);
        }
    }

    private void convertTypes(Set<Class> javaTypes, ModuleConverter tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        Set<Class> preConvertedTypes = new HashSet<>();
        for (Class javaType : javaTypes) {
            if (complexTypeConverter.preConverted(tsModuleSortedMap, javaType)) {
                preConvertedTypes.add(javaType);
            }
        }

        for (Class javaType : preConvertedTypes) {
            complexTypeConverter.convert(javaType);
        }

    }

    private void exploreModelClasses(Set<Class> modelClasses, JavaTypeFilter javaTypeFilter) {

    }

    private void exploreRestClasses(Set<Class> restClasses, JavaTypeFilter javaTypeFilter, Set<Class> modelClasses) {

    }

    private void scanPackages(Set<String> packagesNames, JavaTypeFilter javaTypeFilter, Set<Class> classesSet, Set<Class> enumClasses, Logger logger) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).forPackages(packagesNames.toArray(new String[0])));

        Set<Class<?>> packageClassesSet = reflections.getSubTypesOf(Object.class);
        for (Class packageClass : packageClassesSet) {
            logger.info(String.format("Found class : %s", packageClass.getName()));
            if (javaTypeFilter.filter(packageClass) && packagesNames.contains(packageClass.getPackage().getName())) {
                classesSet.add(packageClass);
            }else{
                logger.warn(String.format("Class filtered out : %s", packageClass.getSimpleName()));
            }
            javaTypeFilter.explain(packageClass, logger, "");
        }

        Set<Class<? extends Enum>> packageEnumsSet = reflections.getSubTypesOf(Enum.class);
        for (Class packageEnumClass : packageEnumsSet) {
            if (packagesNames.contains(packageEnumClass.getPackage().getName())) {
                logger.info(String.format("Found enum class : %s", packageEnumClass.getName()));
                enumClasses.add(packageEnumClass);
            }
        }
    }
}
