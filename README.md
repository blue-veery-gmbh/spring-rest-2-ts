# Purpose
spring-rest2ts-generator generates TypeScript code based on spring mvc REST controllers and object model used in HTTP requests and responses.
From model classes there are generated TypeScript interfaces and from REST controllers full working Angular services based on Observable API
or plain JavaScript services based on Promises API. The main idea is that Spring annotations describing REST endpoints have enough information
to compose HTTP request and call REST endpoints so TypeScript code could be automatically generated. Such generated code reduce amount 
of hand written code on frontend and gives type safe API to backend, changes in URL path are hidden, if there is refactored REST endpoint, 
generated code will reflect these changes which will cause compile time error which safes time on testing 

# Basic Configuration
## Configuration example
Due to greater flexibility spring-rest2ts-generator is configured by code, any configuration files are not needed. 
This gives possibility to easily extends generator in places where it is needed
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
Generator need two filters to select java classes for which TypeScript types will be generated, these filters 
allows to skip Java classes in given packages for which TypeScript generation is not needed. By default Rest2tsGenerator
for model classes and rest controllers has RejectJavaTypeFilter which rejects all classes in given java packages,
such filter could be used to skip generation for REST controllers when only Typescript types are need for Java model classes
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
object mapper is applying some mapping some of them are required for example in JSON there in type for Date so it must be
represented as string or number, based on some default configuration or dedicated annotations. Currently Jackson Object
mapper is supported. JacksonObjectMapper allows to set fields, getters and setters visibility based on this Typescript 
interface fields are generated. From jackson there are supported following annotations  :
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
   + Angular4ImplementationGenerator - generates valid Angular services with methods which corresponds to REST endpoints, 
   they have wrapped return type into Observable  
   + FetchBasedImplementationGenerator - generates valid plain JavaScript classes with methods which corresponds to REST endpoints, 
                                         they have wrapped return type into Promise  
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
modules will be generated. There are required only root packages if there following packages:
 + com.blueveery.springrest2ts.examples.models
 + com.blueveery.springrest2ts.examples.ctrls
it is is enough to list package `com.blueveery.springrest2ts.examples.ctrls`. Generator discovers also nested classes,
 if model classes or REST controllers are using such classes, adequate TypeScript classes/interfaces will be generated
 
## Examples
Module spring-rest2ts-examples contains few model classes and REST controllers, class TsCodeGenerationsTest contains few 
ready to run configuration examples (they are not unit tests, just examples), each example generates code to directory
`target/classes/test-webapp/src`, its parent directory `target/classes/test-webapp` contains webpack and npm setup 
which is valid for all generator configuration apart from `configurableTsModulesConverter` which has diffrent entry points due to changed module 
names. To compile generated TypeScript code just execute following commands in folder `target/classes/test-webapp`:
```
    npm install
    npm run build
    
```
or just review generated code in Your favourite IDE which supports TypeScript

To execute calls by use of generated code first start java backend and next execute command
```
    npm install
    npm run build
    npm test
    
```

There are few tests in MochaJS for TypeScript services based on Observable as well as based on  Promises

 

# Advanced configuration

## Modules converters
Modules converter defines in which TypeScript module should be placed generated TypeScript type for given Java class
There two types of modules converters:
  + TsModuleCreatorConverter
  + ConfigurableTsModulesConverter
### TsModuleCreatorConverter
By default generator is using TsModuleCreatorConverter, this converter takes few last subpackages from java package and joins
them using hyphen. For java package `com.blueveery.springrest2ts.examples.ctrls` it will be create TS module `examples-ctrls`
if this converter is configured to use last two subpackages. This converter will create module for any found java class.
```java
    TsModuleCreatorConverter moduleConverter = new TsModuleCreatorConverter(3); // use three last subpackges
    tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter); 
```

Imports between generated types are using path from root so in tsconfig.json in web project iin which generated code 
is used baseUrl must be defined as it is in examples.

### ConfigurableTsModulesConverter
ConfigurableTsModulesConverter takes as an input maaping from java packages to typeScript module which allows to specify 
name of each generated module. This generator takes optionally as input also  TsModuleCreatorConverter to generates modules
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
Angular services should have postfix "Service" to handle this situation type name mappers are provided. Currently there is 
only one implementation `SubstringClassNameMapper` but any one could crete new one by implementing interface 'ClassNameMapper'
Each class converter is using name mapper so they can be defined for enum, model and REST classes
```java
    modelClassesConverter.setClassNameMapper(new SubstringClassNameMapper("DTO", ""));
    restClassesConverter.setClassNameMapper(new SubstringClassNameMapper("Ctrl", "Service"));
```
## Custom type mapping
During serialization some types are converted by default, such conversion does not depends on any annotations to learn how
convert types. For example UUID could be converted to string, Date to number or string or array of numbers. To handle such 
situation generator has map of custom mapping which allows to define new such mappings:
```java
    tsGenerator.getCustomTypeMapping().put(UUID.class, TypeMapper.tsString);
    tsGenerator.getCustomTypeMapping().put(BigInteger.class, TypeMapper.tsNumber);
    tsGenerator.getCustomTypeMapping().put(LocalDateTime.class, TypeMapper.tsNumber);
    tsGenerator.getCustomTypeMapping().put(LocalDate.class, new TSArray(TypeMapper.tsNumber));
```
Class TypeMapper has static fields for all base TypeScript types, and for others caseses types could be created using TS model
like for array of numbers `new TSArray(TypeMapper.tsNumber)`

## Nullable types
TypeScript has great feature to warn about cases where value could be null to benefit from it TS compiler option must be set
`strictNullChecks` and proper fields in model set as nullable or proper parameters in methods which makes REST calls. 
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
gives possibility to extend conversion with new functionality. Now there is provided one such listener `SwaggerConversionListener`
which adds comments to TypeScript types based on swagger 2.0 `io.swagger.oas.annotations.Operation` annotation 
```java
    restClassesConverter.getConversionListener().getConversionListenerSet().add(new SwaggerConversionListener());
```

## Some special cases
### Model class getter and setter types differs
In most cases such situation is just a bug but there could be situations in which this required to handle this TypeScript filed
is an union of getter and setter type, in this order. Special case of this situation is when getter or setter is missing
for missing setter field in TypeScript is marked as readonly for missing getter (for example we send password but do not read them from server)
TypeScript field is union of `undefined | <setter type>` because after read from server such fields will have value of undefined

### Java REST controllers overloaded methods
Java allows for overloaded methods which is not supported by TypeScript/JavaScript, to solve this problems if overloaded methods are met
in REST controller, generated method names in TypeScript are changed by appending in first round HTTP methods to TypeScript method name 
if they differ, if not URL path is appended splitted on `/` and joined with `_`

## Maven repository 
  

 



    

    
    
    
    
    
    
    
    





