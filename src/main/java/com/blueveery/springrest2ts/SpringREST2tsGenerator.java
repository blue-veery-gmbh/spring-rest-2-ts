package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
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

    private Set<Class> modelClassesConditions = new HashSet<>();
    private Set<Class> restClassesConditions = new HashSet<>();
    private Set<String> packagesNames = new HashSet<>();
    private Map<Class, TSType> customTypeMapping = new HashMap<>();
    private GenerationContext generationContext;
    private ModuleConverter moduleConverter;

    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    public Set<Class> getModelClassesConditions() {
        return modelClassesConditions;
    }

    public Set<Class> getRestClassesConditions() {
        return restClassesConditions;
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

    public void setModuleConverter(ModuleConverter moduleConverter) {
        this.moduleConverter = moduleConverter;
    }

    public SortedSet<TSModule> generate(ModuleConverter moduleConverter, Path outputDir) throws IOException {
        Set<Class> modelClasses = new HashSet<>();
        Set<Class> restClasses = new HashSet<>();
        Set<Class> enumClasses = new HashSet<>();
        Set<Class> modelBaseClassesConditions = new HashSet<>();
        Set<Class> modelAnnotationsConditions = new HashSet<>();
        splitClasses(modelClassesConditions, modelBaseClassesConditions, modelAnnotationsConditions);

        Set<Class> restBaseClassesConditions = new HashSet<>();
        Set<Class> restAnnotationsConditions = new HashSet<>();
        splitClasses(restClassesConditions, restBaseClassesConditions, restAnnotationsConditions);

        scanPackages(packagesNames, modelBaseClassesConditions, modelAnnotationsConditions, modelClasses, enumClasses);
        scanPackages(packagesNames, restBaseClassesConditions, restAnnotationsConditions, restClasses, enumClasses);


        registerCustomTypesMapping(customTypeMapping);

        exploreRestClasses(restClasses, modelBaseClassesConditions, modelAnnotationsConditions, modelClasses);
        exploreModelClasses(modelClasses, modelBaseClassesConditions, modelAnnotationsConditions);

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

    private void exploreModelClasses(Set<Class> modelClasses, Set<Class> modelBaseClassesConditions, Set<Class> modelAnnotationsConditions) {

    }

    private void exploreRestClasses(Set<Class> restClasses, Set<Class> modelBaseClassesConditions, Set<Class> modelAnnotationsConditions, Set<Class> modelClasses) {

    }

    private void scanPackages(Set<String> packagesNames, Set<Class> baseClassesConditions, Set<Class> annotationsConditions, Set<Class> classesSet, Set<Class> enumClasses) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).forPackages(packagesNames.toArray(new String[0])));

        Set<Class<?>> packageClassesSet = reflections.getSubTypesOf(Object.class);
        for (Class packageClass : packageClassesSet) {
            if (classConditionsAreMet(baseClassesConditions, annotationsConditions, packageClass)) {
                classesSet.add(packageClass);
            }
        }

        Set<Class<? extends Enum>> packageEnumsSet = reflections.getSubTypesOf(Enum.class);
        for (Class packageEnumClass : packageEnumsSet) {
            enumClasses.add(packageEnumClass);
        }
    }

    private boolean classConditionsAreMet(Set<Class> baseClassesConditions, Set<Class> annotationsConditions, Class packageClass) {
        for (Class baseClass : baseClassesConditions) {
            if (baseClass.isAssignableFrom(packageClass)) {
                return true;
            }
        }

        for (Class annotationClass : annotationsConditions) {
            if (packageClass.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    private void splitClasses(Set<Class> classesConditions, Set<Class> baseClassesConditions, Set<Class> annotationsConditions) {
        for (Class classCondition : classesConditions) {
           if (classCondition.isAnnotation()) {
                annotationsConditions.add(classCondition);
            } else {
                baseClassesConditions.add(classCondition);
            }
        }
    }
}
