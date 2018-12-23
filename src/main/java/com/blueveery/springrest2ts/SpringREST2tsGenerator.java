package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.tsmodel.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class SpringREST2tsGenerator {

    private JavaTypeFilter modelClassesCondition;
    private JavaTypeFilter restClassesCondition;
    private Set<String> packagesNames = new HashSet<>();
    private Map<Class, TSType> customTypeMapping = new HashMap<>();
    private GenerationContext generationContext;

    public void setModelClassesCondition(JavaTypeFilter modelClassesCondition) {
        this.modelClassesCondition = modelClassesCondition;
    }

    public void setRestClassesCondition(JavaTypeFilter restClassesCondition) {
        this.restClassesCondition = restClassesCondition;
    }

    public Set<String> getPackagesNames() {
        return packagesNames;
    }

    public Map<Class, TSType> getCustomTypeMapping() {
        return customTypeMapping;
    }

    public void setGenerationContext(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public SortedSet<TSModule> generate(ModuleConverter moduleConverter, Path outputDir) throws IOException {
        Set<Class> modelClasses = new HashSet<>();
        Set<Class> restClasses = new HashSet<>();
        Set<Class> enumClasses = new HashSet<>();

        scanPackages(packagesNames, modelClassesCondition, modelClasses, enumClasses);
        scanPackages(packagesNames, restClassesCondition, restClasses, enumClasses);


        registerCustomTypesMapping(customTypeMapping);

        exploreRestClasses(restClasses, modelClassesCondition, modelClasses);
        exploreModelClasses(modelClasses, restClassesCondition);

        generationContext.getDefaultImplementationGenerator().generateImplementationSpecificUtilTypes(generationContext, moduleConverter);


        convertModules(enumClasses, moduleConverter);
        convertModules(modelClasses, moduleConverter);
        convertModules(restClasses, moduleConverter);

        convertTypes(enumClasses, moduleConverter, new EnumConverter());
        convertTypes(modelClasses, moduleConverter, new ModelClassToTsConverter());
        convertTypes(restClasses, moduleConverter, new SpringRestToTsConverter());

        writeTypeScriptTypes(moduleConverter.getTsModules(), generationContext, outputDir);

        return moduleConverter.getTsModules();
    }

    private void registerCustomTypesMapping(Map<Class, TSType> customTypeMapping) {
        for (Class nextJavaType : customTypeMapping.keySet()) {
            TSType tsType = customTypeMapping.get(nextJavaType);
            TypeMapper.registerTsType(nextJavaType, tsType);
        }
    }

    private void writeTypeScriptTypes(SortedSet<TSModule> tsModuleSortedSet, GenerationContext context, Path outputDir) throws IOException {
        for (TSModule tsModule : tsModuleSortedSet) {
            tsModule.writeModule(context, outputDir);
        }
    }

    private void convertModules(Set<Class> javaClasses, ModuleConverter moduleConverter) {
        for (Class javaType : javaClasses) {
            moduleConverter.mapJavaTypeToTsModule(javaType);
        }
    }

    private void convertTypes(Set<Class> javaTypes, ModuleConverter tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        for (Class javaType : javaTypes) {
            complexTypeConverter.preConvert(tsModuleSortedMap, javaType);
        }

        for (Class javaType : javaTypes) {
            complexTypeConverter.convert(tsModuleSortedMap, generationContext, javaType);
        }

    }

    private void exploreModelClasses(Set<Class> modelClasses, JavaTypeFilter javaTypeFilter) {

    }

    private void exploreRestClasses(Set<Class> restClasses, JavaTypeFilter javaTypeFilter, Set<Class> modelClasses) {

    }

    private void scanPackages(Set<String> packagesNames, JavaTypeFilter javaTypeFilter, Set<Class> classesSet, Set<Class> enumClasses) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).forPackages(packagesNames.toArray(new String[0])));

        Set<Class<?>> packageClassesSet = reflections.getSubTypesOf(Object.class);
        for (Class packageClass : packageClassesSet) {
            if (javaTypeFilter.filter(packageClass)) {
                classesSet.add(packageClass);
            }
        }

        Set<Class<? extends Enum>> packageEnumsSet = reflections.getSubTypesOf(Enum.class);
        for (Class packageEnumClass : packageEnumsSet) {
            enumClasses.add(packageEnumClass);
        }
    }
}
