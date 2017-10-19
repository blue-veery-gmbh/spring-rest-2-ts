package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.tsmodel.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class SpringREST2tsGenerator {

    public static final String CUSTOM_MODULE_RAI_URL_SERVICE = "rai-url-service";

    private GenerationContext generationContext;
    private ModuleConverter moduleConverter = new DefaultModuleConverter(2);;

    private ClassLoader getClassLoader(){
        return this.getClass().getClassLoader();
    }

    public void setGenerationContext(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public void setModuleConverter(ModuleConverter moduleConverter) {
        this.moduleConverter = moduleConverter;
    }

    public void loadAndGenerate(Set<String> packagesNames, Set<String> modelClassNamesConditions, Set<String> restClassNamesConditions, File outputDir) throws ClassNotFoundException, IOException {
        Set<Class> modelClassesConditions = loadClasses(modelClassNamesConditions);
        Set<Class> restClassesConditions = loadClasses(restClassNamesConditions);

        generate(packagesNames, modelClassesConditions, restClassesConditions, null, outputDir);
    }

    public void generate(Set<String> packagesNames, Set<Class> modelClassesConditions, Set<Class> restClassesConditions, Map<Class, String> customTypeMapping, File outputDir) throws IOException {
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


        convertModules(enumClasses, tsModuleMap, moduleConverter);
        convertModules(modelClasses, tsModuleMap, moduleConverter);
        convertModules(restClasses, tsModuleMap, moduleConverter);

        convertTypes(enumClasses, tsModuleMap, new EnumConverter());
        convertTypes(modelClasses, tsModuleMap, new ModelClassToTsConverter());
        convertTypes(restClasses, tsModuleMap, new SpringRestToTsConverter());

        writeTypeScriptTypes(tsModuleMap, generationContext, outputDir);
    }

    public void generateCustomModule(String name, File outputDir) throws IOException{
        switch (name) {
            case CUSTOM_MODULE_RAI_URL_SERVICE:
                SortedMap<String, TSModule> tsModuleMap = new TreeMap<>();
                TSModule raiUrlServiceModule = new TSModule(CUSTOM_MODULE_RAI_URL_SERVICE, false);
                TSClass raiUrlService = new TSClass("UrlService", raiUrlServiceModule);
                raiUrlService.addTsMethod(
                        new TSMethod("getBackendUrl",
                                raiUrlService,
                                new TSSimpleType("string"),
                                false,
                                false)
                );
                raiUrlService.addTsMethod(
                    new TSMethod("constructor",
                            raiUrlService,
                            null,
                            false,
                            true)
                );
                generationContext.getImplementationGenerator().addComplexTypeUsage(raiUrlService);
                raiUrlServiceModule.addScopedType(raiUrlService);
                tsModuleMap.put(CUSTOM_MODULE_RAI_URL_SERVICE, raiUrlServiceModule);
                writeTypeScriptTypes(tsModuleMap, generationContext, outputDir);
                break;
            default:
        }
    }

    private void registerCustomTypesMapping(Map<Class, String> customTypeMapping, SortedMap<String, TSModule> tsModuleSortedMap) {
        for(Class nextJavaType:customTypeMapping.keySet()){
            String tsTypeName = customTypeMapping.get(nextJavaType);
            int typeNameStartIndex = tsTypeName.lastIndexOf("\\.");
            if(typeNameStartIndex>0) {
                String tsShortTypeName = tsTypeName.substring(typeNameStartIndex);
                String tsModuleName = tsTypeName.substring(0, typeNameStartIndex);
                TSModule tsModule = tsModuleSortedMap.values().stream().findFirst().filter(m -> tsModuleName.equals(m.getName())).orElseGet(null);
                if(tsModule==null) {
                    tsModule = new TSModule(tsModuleName, true);
                }
                tsModuleSortedMap.put(nextJavaType.getPackage().getName(), tsModule);
                TSComplexType tsComplexType = new TSClass(tsShortTypeName, tsModule);
                tsModule.addScopedType(tsComplexType);
                TypeMapper.registerTsType(nextJavaType, tsComplexType);
            }else {
                TypeMapper.registerTsType(nextJavaType, new TSSimpleType(tsTypeName));
            }
        }
    }

    private void writeTypeScriptTypes(SortedMap<String, TSModule> tsModuleSortedMap, GenerationContext context, File outputDir) throws IOException {
        if(!outputDir.exists()){
            outputDir.mkdirs();
        }

        for(TSModule tsModule:tsModuleSortedMap.values()){
            File tsModuleFile = new File(outputDir, tsModule.getName()+".ts");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tsModuleFile));
            tsModule.write(context, writer);
            writer.close();
        }
    }

    private void convertModules(Set<Class> javaClasses, SortedMap<String, TSModule> tsModulesMap, ModuleConverter moduleConverter) {
        for(Class javaType:javaClasses){
            moduleConverter.convert(javaType.getPackage().getName(), tsModulesMap);
        }
    }

    private void convertTypes(Set<Class> javaTypes, SortedMap<String, TSModule> tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        for(Class javaType:javaTypes){
            complexTypeConverter.preConvert(tsModuleSortedMap, javaType);
        }

        for(Class javaType:javaTypes){
            complexTypeConverter.convert(tsModuleSortedMap, javaType, generationContext.getImplementationGenerator());
        }

    }

    private void exploreModelClasses(Set<Class> modelClasses, Set<Class> modelBaseClassesConditions, Set<Class> modelAnnotationsConditions) {
        
    }

    private void exploreRestClasses(Set<Class> restClasses, Set<Class> modelBaseClassesConditions, Set<Class> modelAnnotationsConditions, Set<Class> modelClasses) {

    }

    private void scanPackages(Set<String> packagesNames, Set<Class> baseClassesConditions, Set<Class> annotationsConditions, Set<Class> classesSet, Set<Class> enumClasses) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).forPackages(packagesNames.toArray(new String[0])));

        Set<Class<?>> packageClassesSet = reflections.getSubTypesOf(Object.class);
        for(Class packageClass:packageClassesSet){
           if(classConditionsAreMet(baseClassesConditions, annotationsConditions, packageClass)){
                classesSet.add(packageClass);
            }
        }

        Set<Class<? extends Enum>> packageEnumsSet = reflections.getSubTypesOf(Enum.class);
        for(Class packageEnumClass:packageEnumsSet){
            enumClasses.add(packageEnumClass);
        }
    }

    private boolean classConditionsAreMet(Set<Class> baseClassesConditions, Set<Class> annotationsConditions, Class packageClass) {
        for (Class baseClass:baseClassesConditions){
            if (baseClass.isAssignableFrom(packageClass)) {
                return true;
            }
        }

        for(Class annotationClass:annotationsConditions){
            if(packageClass.isAnnotationPresent(annotationClass)){
                return true;
            }
        }
        return false;
    }

    private void splitClasses(Set<Class> classesConditions, Set<Class> baseClassesConditions, Set<Class> annotationsConditions) {
        for(Class classCondition:classesConditions){
            if(classCondition.isAnnotation()){
                annotationsConditions.add(classCondition);
            }else {
                baseClassesConditions.add(classCondition);
            }
        }
    }

    private Set<Class> loadClasses(Set<String> classNamesConditions) throws ClassNotFoundException {
        Set<Class> classSet = new HashSet<>();
        for(String className:classNamesConditions){
            classSet.add(getClassLoader().loadClass(className));
        }
        return classSet;
    }
}
