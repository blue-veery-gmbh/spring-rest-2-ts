package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.tsmodel.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class SpringREST2tsGenerator {

    private Set<Class> modelClassesConditions = new HashSet<>();
    private Set<Class> restClassesConditions = new HashSet<>();
    private Set<String> packagesNames = new HashSet<>();
    private Map<Class, String> customTypeMapping = new HashMap<>();
    private GenerationContext generationContext;
    private ModuleConverter moduleConverter = new DefaultModuleConverter(2);

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

    public Map<Class, String> getCustomTypeMapping() {
        return customTypeMapping;
    }

    public void setGenerationContext(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public void setModuleConverter(ModuleConverter moduleConverter) {
        this.moduleConverter = moduleConverter;
    }

    public SortedMap<String, TSModule> generate(Path outputDir) throws IOException {
        SortedMap<String, TSModule> tsModuleMap = new TreeMap<>();
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


        registerCustomTypesMapping(customTypeMapping, tsModuleMap);

        exploreRestClasses(restClasses, modelBaseClassesConditions, modelAnnotationsConditions, modelClasses);
        exploreModelClasses(modelClasses, modelBaseClassesConditions, modelAnnotationsConditions);

        generationContext.getDefaultImplementationGenerator().generateImplementationSpecificUtilTypes(generationContext,tsModuleMap);


        convertModules(enumClasses, tsModuleMap, moduleConverter);
        convertModules(modelClasses, tsModuleMap, moduleConverter);
        convertModules(restClasses, tsModuleMap, moduleConverter);

        convertTypes(enumClasses, tsModuleMap, new EnumConverter());
        convertTypes(modelClasses, tsModuleMap, new ModelClassToTsConverter());
        convertTypes(restClasses, tsModuleMap, new SpringRestToTsConverter());

        writeTypeScriptTypes(tsModuleMap, generationContext, outputDir);

        return tsModuleMap;
    }

    private void registerCustomTypesMapping(Map<Class, String> customTypeMapping, SortedMap<String, TSModule> tsModuleSortedMap) {
        for (Class nextJavaType : customTypeMapping.keySet()) {
            String tsTypeName = customTypeMapping.get(nextJavaType);
            int typeNameStartIndex = tsTypeName.lastIndexOf("\\.");
            if (typeNameStartIndex > 0) {
                String tsShortTypeName = tsTypeName.substring(typeNameStartIndex);
                String tsModuleName = tsTypeName.substring(0, typeNameStartIndex);
                TSModule tsModule = tsModuleSortedMap.values().stream().findFirst().filter(m -> tsModuleName.equals(m.getName())).orElseGet(null);
                if (tsModule == null) {
                    tsModule = new TSModule(tsModuleName, true);
                }
                tsModuleSortedMap.put(nextJavaType.getPackage().getName(), tsModule);
                TSComplexType tsComplexType = new TSClass(tsShortTypeName, tsModule);
                tsModule.addScopedType(tsComplexType);
                TypeMapper.registerTsType(nextJavaType, tsComplexType);
            } else {
                TypeMapper.registerTsType(nextJavaType, new TSSimpleType(tsTypeName));
            }
        }
    }

    private void writeTypeScriptTypes(SortedMap<String, TSModule> tsModuleSortedMap, GenerationContext context, Path outputDir) throws IOException {
        if (Files.notExists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        for (TSModule tsModule : tsModuleSortedMap.values()) {
            Path tsModuleFile = outputDir.resolve(tsModule.getName() + ".ts");
            BufferedWriter writer = Files.newBufferedWriter(tsModuleFile);
            tsModule.write(context, writer);
            writer.close();
        }
    }

    private void convertModules(Set<Class> javaClasses, SortedMap<String, TSModule> tsModulesMap, ModuleConverter moduleConverter) {
        for (Class javaType : javaClasses) {
            moduleConverter.convert(javaType.getPackage().getName(), tsModulesMap);
        }
    }

    private void convertTypes(Set<Class> javaTypes, SortedMap<String, TSModule> tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        for (Class javaType : javaTypes) {
            complexTypeConverter.preConvert(tsModuleSortedMap, javaType);
        }

        for (Class javaType : javaTypes) {
            complexTypeConverter.convert(tsModuleSortedMap, javaType, generationContext);
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
