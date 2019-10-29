# Spring rest2ts generator 
spring-rest2ts-generator generator generates TypeScript code based on spring mvc REST controllers and data model for HTTP requests and responses. 
From model classes there are generated TypeScript interfaces and from REST controllers full working Angular services based on Observable API
or plain JavaScript services based on Promises API. The main idea is that Spring annotations describing REST endpoints have enough information 
to compose HTTP request and call REST endpoints so TypeScript code could be automatically generated. Such generated code reduces the amount 
of handwritten code on the frontend and gives type-safe API to the backend, changes in URL path are hidden, in case of REST endpoint refactoring, 
generated code will reflect these changes which will cause compile-time error in the web app which reduces time on testing

### Supported features:
   + Java Beans convention for data model mapping
   + FasterXML/Jackson annotations for data model mapping
   + Custom type Mappings
   + Java collections mapped into TS arrays     
   + Java Map mapped into TS object
   + Java packages to module conversion
   + Java enums mapped as TS enums or union types   
   + Inheritance mapping    
   + Name mappings
   + Imports between generated TS modules 
   + Spring REST annotations based on which TS services are generated
   + TS services for Angular framework
   + TS services for ReactJS framework   
   + Java class filtering for TS code generation                  
           

# Basic Configuration
## Configuration example
Due to greater flexibility spring-rest2ts-generator is configured by code, no configuration files are needed. 
This gives possibility to easily extend generator in places where it is needed
Here is the simplest generator configurator:

```java
    Rest2tsGenerator tsGenerator = new Rest2tsGenerator();
    
    // Java Classes filtering
    tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(BaseDTO.class));
    tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));
    
    // Java model classes converter setup
    JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
    jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
    modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
    tsGenerator.setModelClassesConverter(modelClassesConverter);
    
    // Spring REST controllers converter
    restClassesConverter = new SpringRestToTsConverter(new Angular4ImplementationGenerator());
    tsGenerator.setRestClassesConverter(restClassesConverter);
    
    // set of java root packages for class scanning
    javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.examples");
    tsGenerator.generate(javaPackageSet, Paths.get("../target/ts-code"));
```
       
## Java Classes filtering 
Generator needs two filters to select java classes for which TypeScript types will be generated, these filters 
allows to skip Java classes in given packages for which TypeScript generation is not needed. By default Rest2tsGenerator
for model classes and rest controllers has RejectJavaTypeFilter which rejects all classes in a given java packages.
Such filter could be used to skip generation for REST controllers when only Typescript types are needed for Java model classes.
There are following filters which allows to build complex conditions:
   + ExtendsJavaTypeFilter
   + HasAnnotationJavaTypeFilter
   + JavaTypePackageFilter
   + ContainsSubStringJavaTypeFilter
   + RegexpJavaTypeFilter
   + RejectJavaTypeFilter
   + OrFilterOperator
   + AndFilterOperator
   + NotFilterOperator
    
## Java model classes converter
Java classes which describe payload model are generated to TypeScript interfaces. During model serialization to JSON, 
object mapper is applying some mappings(some of them are required for example in JSON, Date type must be
represented as string or number) based on some default configuration or dedicated annotations. Currently Jackson Object
mapper is supported. JacksonObjectMapper allows to set fields, getters and setters visibility. Based on this Typescript 
interface fields are generated. There are supported following Jackson annotations  :
   + JsonTypeInfo - TS property is added if type info is serialized to property
   + JsonAutoDetect - overrides default visibility 
   + JsonIgnoreType - properties which have type marked as ignored are skipped 
   + JsonIgnoreProperties - ignores listed properties
   + JsonIgnore - ignores marked property
   + JsonBackReference - ignores TS property
   + JsonProperty - changes property name, based on access marks TS property as readonly
   + JsonSetter - changes property name
   + JsonGetter - changes property name
   + JsonUnwrapped - add fields from property type to the current TS interface
   + JsonValue
   + JsonFormat - changes TS property type, add to TS comment pattern if given
   + JacksonInject - marks TS property as readonly
   + JsonRawValue - changes TS property type to any
   
Java collections are converted into JavaScript arrays, Java Map is converted into object where key has a string type and value is converted 
Typescript Type

## Spring REST controllers converter
From REST classes there is generated working implementation in TypeScript which based on Spring annotations builds complete 
HTTP request. Such implementation depends also used JavaScript framework, there is different approach to HTTP calls in Angular and React
so SpringRestToTsConverter requires instance of ImplementationGenerator. There are available two such implementation generators
   + `Angular4ImplementationGenerator` - generates valid Angular services with methods which corresponds to REST endpoints, 
   they have wrapped return type into Observable  
   + `FetchBasedImplementationGenerator` - generates valid plain JavaScript classes with methods which corresponds to REST endpoints,
   return types are mapped into Promise where it generic type attribute is endpoint return type  
                                         
There are supported following Spring annotations:
   + RequestMapping
   + GetMapping 
   + PostMapping
   + PutMapping 
   + DeleteMapping
   + PatchMapping
   + PathVariable
   + RequestParam
   + RequestBody
## Response content conversion
 + If REST endpoint produces JSON generated service method returns Observable or `Promise<<Mapped java type>>`
 + If REST endpoint doesn't produce response it should return response code 204(no content) 
 generated service method returns Observable or `Promise<void>`
 + If REST endpoint returns java primitive types like string, numbers or booleans there is made conversion 
to proper JavaScript type, for such REST endpoint required content type is text. 

## Final step in generator configuration
Generator takes as input set of java packages which should be scanned for java classes and output path where TypesScript 
modules will be generated. There are required only root packages in case if following packages are present: 
 + com.blueveery.springrest2ts.examples.models
 + com.blueveery.springrest2ts.examples.ctrls
it is is enough to list package `com.blueveery.springrest2ts.examples.ctrls`. Generator discovers also nested classes,
 if model classes or REST controllers are using such classes, adequate TypeScript classes/interfaces will be generated
 
## Examples
Module spring-rest2ts-examples contains few model classes and REST controllers, class TsCodeGenerationsTest and ExtendedTsCodeGenerationsTest contains few 
ready to run configuration examples (they are not unit tests, just examples), each example generates code to directory
`target/classes/test-webapp/src`, its parent directory `target/classes/test-webapp` contains webpack and npm setup 
which is valid for all generator configurations apart from `configurableTsModulesConverter` which has different entry points 
due to changed module names. To compile generated TypeScript code just execute following commands in folder `target/classes/test-webapp`:
```
    npm install
    npm run build
    
```
or just review generated code in Your favourite IDE which supports TypeScript

# Advanced configuration

## Modules converters
Modules converter defines, in which TypeScript module, should be placed generated TypeScript type for given Java class
There are two types of modules converters:
  + TsModuleCreatorConverter
  + ConfigurableTsModulesConverter
### TsModuleCreatorConverter
By default generator is using TsModuleCreatorConverter, this converter takes a few last subpackages from java package and joins
them using hyphen. For java package `com.blueveery.springrest2ts.examples.ctrls` it will create TS module `examples-ctrls`
if this converter is configured to use last two subpackages. This converter will create module for any found java class.
```java
    TsModuleCreatorConverter moduleConverter = new TsModuleCreatorConverter(3); // use three last subpackges
    tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter); 
```

Imports between generated types are using path from root so in tsconfig.json, in the web project, in which generated code 
is used, baseUrl must be defined as it is in examples.

### ConfigurableTsModulesConverter
ConfigurableTsModulesConverter takes as an input maping from java packages to typeScript module which allows to specify 
name of each generated module. This generator optionally as an input takes also  TsModuleCreatorConverter to generate modules
for packages which are missing in the mapping
```java
    HashMap<String, TSModule> packagesMap = new HashMap<>();
    packagesMap.put("com.blueveery.springrest2ts.examples.model", new TSModule("model", Paths.get("app/sdk/model"), false));
    TSModule servicesModule = new TSModule("services", Paths.get("app/sdk/services"), false);
    packagesMap.put("com.blueveery.springrest2ts.examples.ctrls.core", servicesModule);
    packagesMap.put("com.blueveery.springrest2ts.examples.ctrls", servicesModule);
    ConfigurableTsModulesConverter moduleConverter = new ConfigurableTsModulesConverter(packagesMap);
    tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter);

```

## Enums converters
Java enums could be serialized as strings or ordinals. If they are serialized to strings in TypesScript union of enum values 
could be used, if enums are serialized to ordinals TypesScript enums could be generated. To handle this there are provided two
enums converters
  + JavaEnumToTsUnionConverter
  + JavaEnumToTsEnumConverter
  
and configured in code:
```java
    tsGenerator.setEnumConverter(new JavaEnumToTsUnionConverter());
```
or
```java
    tsGenerator.setEnumConverter(new JavaEnumToTsEnumConverter());
```
  
## Type names mapping
Between Java and TypeScript there are different naming conventions for example model classes could end with postfix "DTO"
which could be unwanted in generated Typescript model, REST controllers could have "Ctrl" postfix where generated 
Angular services should have postfix "Service". To handle this situation type name mappers are provided. Currently there is 
only one implementation `SubstringClassNameMapper` but any one could crete new one by implementing interface 'ClassNameMapper'
Each class converter is using name mapper so they can be defined for enum, model and REST classes
```java
    modelClassesConverter.setClassNameMapper(new SubstringClassNameMapper("DTO", ""));
    restClassesConverter.setClassNameMapper(new SubstringClassNameMapper("Ctrl", "Service"));
```
## Custom type mapping
During serialization some types are converted by default, such conversion does not depends on any annotations to learn how
convert types. For example UUID could be converted to string, Date to number or string/array of numbers. To handle such 
situation generator has map of custom mapping which allows to define such new mappings:
```java
    tsGenerator.getCustomTypeMapping().put(UUID.class, TypeMapper.tsString);
    tsGenerator.getCustomTypeMapping().put(BigInteger.class, TypeMapper.tsNumber);
    tsGenerator.getCustomTypeMapping().put(LocalDateTime.class, TypeMapper.tsNumber);
    tsGenerator.getCustomTypeMapping().put(LocalDate.class, new TSArray(TypeMapper.tsNumber));
```
Class TypeMapper has static fields for all base TypeScript types, and for other cases types could be created using TS model
like for array of numbers `new TSArray(TypeMapper.tsNumber)`

## Nullable types
TypeScript has s great feature to warn about cases where value could be null. To use it TS compiler option must be set
`strictNullChecks` and proper fields in model set as nullable or proper parameters in methods which make REST calls. 
Generators support this by marking TS fields based on information taken from corresponding java classes. Generator is using 
`NullableTypesStrategy` with default implementation `DefaultNullableTypesStrategy`
which marks types as nullable if
  + field or method parameter type is wrapped in Java `java.util.Optional`
  + field or method parameter is marked with annotation `javax.annotation.Nullable`
  + field or method parameter has type which is Java wrapper for primitive types
Marks mean that filed type is union of original field type and null; 
 
`DefaultNullableTypesStrategy` has settings which allows to configure which of above option use, by default all they are used
```java
    DefaultNullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();
    nullableTypesStrategy.setUsePrimitiveTypesWrappers(false);
    tsGenerator.setNullableTypesStrategy(nullableTypesStrategy);
```

## Conversion Listeners
Each converter( enum, model classes, rest classes) allows for registering conversion listeners, such listeners 
give possibility to extend conversion with new functionality. Now there is one, such listener provided `SwaggerConversionListener`
which adds comments to TypeScript types based on swagger 2.0 `io.swagger.oas.annotations.Operation` annotation 
```java
    restClassesConverter.getConversionListener().getConversionListenerSet().add(new SwaggerConversionListener());
```

## Java compiler setup
Java compiler by default optimizes methods parameters names, to have readable parameters names in Typescript at least for 
REST controllers modules this optimization should be switched off
```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <compilerArgs>
                    <compilerArg>-parameters</compilerArg>
                </compilerArgs>
            </configuration>
        </plugin>
``` 

## Some special cases
### Model class getter and setter types differs
In most cases such situation is just a bug but there could be a situations in which this required to handle this TypeScript filed
is an union of getter and setter type, in this order. Special case of this situation is when getter or setter is missing.
For missing setter field in TypeScript it is marked as readonly. For missing getter (for example we send password but do not read them from server)
TypeScript field is union of `undefined | <setter type>` because after read from server such fields will have value of undefined

### Java REST controllers overloaded methods
Java allows for overloaded methods what is not supported by TypeScript/JavaScript. To solve this problems if overloaded methods are met
in REST controller, generated method names in TypeScript are changed by appending in first round HTTP methods to TypeScript method name 
if they differ, if not URL path is appended splitted on `/` and joined with `_`

## Unsupported mappings, coming soon...
  + multipart mapping `RequestPart`
  + spring data Pageable and Page
  + java generic types    
 
