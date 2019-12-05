package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.extensions.ConversionExtension;
import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import com.blueveery.springrest2ts.filters.RejectJavaTypeFilter;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class Rest2tsGenerator {

    static Logger logger = LoggerFactory.getLogger("gen-logger");
    private Map<Class, TSType> customTypeMapping = new HashMap<>();

    private JavaTypeFilter modelClassesCondition = new RejectJavaTypeFilter();
    private JavaTypeFilter restClassesCondition = new RejectJavaTypeFilter();

    private NullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();

    private JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = new TsModuleCreatorConverter(2);
    private ComplexTypeConverter enumConverter = new JavaEnumToTsEnumConverter();;
    private ModelClassesAbstractConverter modelClassesConverter;
    private RestClassConverter restClassesConverter;

    public Map<Class, TSType> getCustomTypeMapping() {
        return customTypeMapping;
    }

    public void setModelClassesCondition(JavaTypeFilter modelClassesCondition) {
        this.modelClassesCondition = modelClassesCondition;
    }

    public void setRestClassesCondition(JavaTypeFilter restClassesCondition) {
        this.restClassesCondition = restClassesCondition;
    }

    public void setJavaPackageToTsModuleConverter(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter) {
        this.javaPackageToTsModuleConverter = javaPackageToTsModuleConverter;
    }

    public void setEnumConverter(ComplexTypeConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    public void setModelClassesConverter(ModelClassesAbstractConverter modelClassesConverter) {
        this.modelClassesConverter = modelClassesConverter;
    }

    public void setRestClassesConverter(RestClassConverter restClassesConverter) {
        this.restClassesConverter = restClassesConverter;
    }

    public void setNullableTypesStrategy(NullableTypesStrategy nullableTypesStrategy) {
        this.nullableTypesStrategy = nullableTypesStrategy;
    }

    public SortedSet<TSModule> generate(Set<String> inputPackagesNames, Path outputDir) throws IOException {
        Set<Class> modelClasses = new HashSet<>();
        Set<Class> restClasses = new HashSet<>();
        Set<Class> enumClasses = new HashSet<>();
        Set<String> packagesNames = new HashSet<>(inputPackagesNames);
        List<ConversionExtension> conversionExtensionList = new ArrayList<>();

        applyConversionExtension(packagesNames);

        logger.info("Scanning model classes");
        List<Class> loadedClasses= loadClasses(packagesNames);
        searchClasses(loadedClasses, modelClassesCondition, modelClasses, enumClasses, logger);
        logger.info("Scanning rest controllers classes");
        searchClasses(loadedClasses, restClassesCondition, restClasses, enumClasses, logger);


        registerCustomTypesMapping(customTypeMapping);

        exploreRestClasses(restClasses, modelClassesCondition, modelClasses);
        exploreModelClasses(modelClasses, restClassesCondition);

        convertModules(enumClasses, javaPackageToTsModuleConverter);
        convertModules(modelClasses, javaPackageToTsModuleConverter);
        convertModules(restClasses, javaPackageToTsModuleConverter);

        convertTypes(enumClasses, javaPackageToTsModuleConverter, enumConverter);
        if (!modelClasses.isEmpty()) {
            if (modelClassesConverter == null) {
                throw new IllegalStateException("Model classes converter is not set");
            }
            convertTypes(modelClasses, javaPackageToTsModuleConverter, modelClassesConverter);
        }

        if (!restClasses.isEmpty()) {
            if (restClassesConverter == null) {
                throw new IllegalStateException("Rest classes converter is not set");
            }
            convertTypes(restClasses, javaPackageToTsModuleConverter, restClassesConverter);
        }


        writeTSModules(javaPackageToTsModuleConverter.getTsModules(), outputDir, logger);

        return javaPackageToTsModuleConverter.getTsModules();
    }

    private void applyConversionExtension(Set<String> packagesNames) {
        List<JavaTypeFilter> modelClassFilterList = new ArrayList<>();
        List<ModelConversionExtension> modelConversionExtensionList = getModelConversionExtensions();
        for (ModelConversionExtension extension : modelConversionExtensionList) {
            if (extension.getJavaTypeFilter() != null) {
                modelClassFilterList.add(extension.getJavaTypeFilter());
            }
            packagesNames.addAll(extension.getAdditionalJavaPackages());
            modelClassesConverter.getObjectMapperMap().putAll(extension.getObjectMapperMap());
            modelClassesConverter.getConversionListener().getConversionListenerSet().add(extension);
        }

        List<JavaTypeFilter> restClassFilterList = new ArrayList<>();
        if (restClassesConverter != null) {
            for (RestConversionExtension extension : restClassesConverter.getConversionExtensionList()) {
                if (extension.getJavaTypeFilter() != null) {
                    restClassFilterList.add(extension.getJavaTypeFilter());
                }
                packagesNames.addAll(extension.getAdditionalJavaPackages());
                restClassesConverter.getConversionListener().getConversionListenerSet().add(extension);
            }
        }


        if (!modelClassFilterList.isEmpty()) {
            if (modelClassesConverter == null) {
                throw new IllegalStateException("There is installed extension which requires model classes converter");
            }
            modelClassFilterList.add(modelClassesCondition);
            OrFilterOperator orFilterOperator = new OrFilterOperator(modelClassFilterList);
            modelClassesCondition = orFilterOperator;
        }
        if (!restClassFilterList.isEmpty()) {
            if (restClassesConverter == null) {
                throw new IllegalStateException("There is installed extension which requires REST classes converter");
            }
            restClassFilterList.add(restClassesCondition);
            OrFilterOperator orFilterOperator = new OrFilterOperator(restClassFilterList);
            restClassesCondition = orFilterOperator;
        }

    }

    private List<ModelConversionExtension> getModelConversionExtensions() {
        List<ModelConversionExtension> modelConversionExtensionList = new ArrayList<>();
        if (restClassesConverter != null) {
            for (RestConversionExtension restConversionExtension : restClassesConverter.getConversionExtensionList()) {
                ModelConversionExtension modelConversionExtension = restConversionExtension.getModelConversionExtension();
                if (modelConversionExtension != null) {
                    modelConversionExtensionList.add(modelConversionExtension);
                }
            }
        }
        if (modelClassesConverter != null) {
            modelConversionExtensionList.addAll(modelClassesConverter.getConversionExtensionList());
        }
        return modelConversionExtensionList;
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

    private void convertModules(Set<Class> javaClasses, JavaPackageToTsModuleConverter javaPackageToTsModuleConverter) {
        for (Class javaType : javaClasses) {
            javaPackageToTsModuleConverter.mapJavaTypeToTsModule(javaType);
        }
    }

    private void convertTypes(Set<Class> javaTypes, JavaPackageToTsModuleConverter tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        Set<Class> preConvertedTypes = new HashSet<>();
        for (Class javaType : javaTypes) {
            if (complexTypeConverter.preConverted(tsModuleSortedMap, javaType)) {
                preConvertedTypes.add(javaType);
            }
        }

        for (Class javaType : preConvertedTypes) {
            complexTypeConverter.convert(javaType, nullableTypesStrategy);
        }

    }

    private void exploreModelClasses(Set<Class> modelClasses, JavaTypeFilter javaTypeFilter) {

    }

    private void exploreRestClasses(Set<Class> restClasses, JavaTypeFilter javaTypeFilter, Set<Class> modelClasses) {

    }

    private void searchClasses(List<Class> loadedClasses, JavaTypeFilter javaTypeFilter, Set<Class> classSet, Set<Class> enumClassSet, Logger logger) throws IOException {
        for (Class foundClass : loadedClasses) {
            logger.info(String.format("Found class : %s", foundClass.getName()));
            if (Enum.class.isAssignableFrom(foundClass)) {
                logger.info(String.format("Found enum class : %s", foundClass.getName()));
                enumClassSet.add(foundClass);
                continue;
            }

            if (javaTypeFilter.accept(foundClass)) {
                classSet.add(foundClass);
            }else{
                logger.warn(String.format("Class filtered out : %s", foundClass.getSimpleName()));
            }
            javaTypeFilter.explain(foundClass, logger, "");
        }
    }


    private List<Class> loadClasses(Set<String> packageSet) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        List<Class> classList = new ArrayList<>();
        for (String packageName : packageSet) {
            Enumeration<URL> urlEnumeration = classLoader.getResources(packageName.replace(".", "/"));
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                URI uri = null;
                try {
                    uri = url.toURI();
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
                try {
                    FileSystems.newFileSystem(uri, Collections.emptyMap());} catch (Exception ignore) {}
                    Path path = Paths.get(uri);
                    scanPackagesRecursively(classLoader, path, packageName, classList);
            }
        }
        return classList;
    }

    private void scanPackagesRecursively(ClassLoader classLoader, Path currentPath, String packageName, List<Class> classList) throws IOException {
        for (Path nextPath : Files.newDirectoryStream(currentPath)) {
            if (Files.isDirectory(nextPath)) {
                scanPackagesRecursively(classLoader, nextPath, packageName+"."+nextPath.getFileName(), classList);
            } else {
                if (nextPath.toString().endsWith(".class")) {
                    String className = (packageName + "/" + nextPath.getFileName().toString()).replace(".class", "").replace("/", ".");
                    try {
                        Class<?> loadedClass = classLoader.loadClass(className);
                        loadedClass.getSimpleName();
                        if (!loadedClass.isAnnotation()) {
                            addNestedClasses(loadedClass.getDeclaredClasses(), classList);
                            classList.add(loadedClass);
                        }
                    } catch (Error | Exception e) {
                        System.out.println(String.format("Failed to load class %s due to error %s:%s", className, e.getClass().getSimpleName(), e.getMessage()));
                    }
                }
            }
        }
    }

    private void addNestedClasses(Class<?>[] nestedClasses, List<Class> classList) {
        for (Class<?> nestedClass : nestedClasses) {
            if (!nestedClass.isAnnotation()) {
                classList.add(nestedClass);
            }
            addNestedClasses(nestedClass.getDeclaredClasses(), classList);
        }
    }

}
