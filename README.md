# Spring rest2ts generator 
spring-rest2ts-generator generates TypeScript code based on Spring MVC REST controllers and data model for HTTP requests and responses.
 
spring-rest2ts-generator started from Spring MVC but we noticed that it is easy to support also JAX-RS annotations and such support
has been added in version 1.2.4. For model conversion Jackson or Gson annotations could be used.

In version 1.2.4 we also added support for [angular2-jsonapi](https://github.com/ghidoz/angular2-jsonapi) which is a lightweight Angular2+ adapter for JSON API 
 
 
# Features
From model classes there are generated TypeScript interfaces and from REST controllers full working Angular services based on Observable API
or plain JavaScript services based on Promises API. The main idea is that Spring annotations describing REST endpoints have enough information 
to compose HTTP request and call REST endpoints so TypeScript code could be automatically generated. Code created by typescript generator reduces the amount 
of handwritten code on the frontend and gives type-safe API to the backend, changes in URL path are hidden, in case of REST endpoint refactoring, 
generated code will reflect these changes which will avoid compile-time error in the web app which reduces time on testing

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
   + Java generic types mapped to TS generics : since ver 1.2.2    
   + Java interfaces implemented by model classes which contains getters and setter are mapped to TS interfaces to have common types to different TS model classes : since ver 1.2.2    
   + spring data support (Pageable & Page types) : since ver 1.2.2
   + JAX-RS annotation support : since ver 1.2.4
   + model converter which generates TypeScript classes aligned with angular2-jsonapi library       
   + model serializers extension which allows to configure custom JSON serializers/deserializers : since ver 1.2.6       
   + Gson serializer support since version 1.3.0
   
## Installation 
To add a dependency on spring-rest2ts-generator using Maven, use the following:
```xml
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-generator</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-spring</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-spring-data</artifactId>
    <version>1.3.0</version>
    <!-- only if spring data is used-->
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-jackson</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-jax-rs</artifactId>
    <version>1.3.0</version>
    <!-- only if JAX-RS is used-->
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-angular2json-impl</artifactId>
    <version>1.3.0</version>
    <!-- only if angular2json is used-->
</dependency>
<dependency>
    <groupId>com.blue-veery</groupId>
    <artifactId>spring-rest2ts-gson</artifactId>
    <version>1.3.0-SNAPSHOT</version>
    <!-- only if Gson is used-->
</dependency>
```          
           
## Configuration  
Due to greater flexibility typescript generator is configured by code, no configuration files are needed. 
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
## Examples
##### Model classes:
```java
public class BaseDTO {
    private int id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date updateTimeStamp;
}

public class OrderDTO extends BaseDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime orderTimestamp;
}
```

##### Spring REST controller:
```java
@Controller
@RequestMapping("api/order")
public class OrderCtrl {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderDTO entity) {
        return entity;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public OrderDTO getOrder(@PathVariable int id) {
        return new OrderDTO();
    }

}
```
#### Output
##### Typescript model class:
```typescript

export interface Base {
    id: number;
    updateTimeStamp: number;
}


export interface Order extends Base {
    /**
     *    pattern : dd-MM-yyyy hh:mm:ss
     */
    orderTimestamp: string;
}
```

##### Observable based service:
```typescript
@Injectable()
export class OrderService {
    httpService: HttpClient;


    public constructor(httpService: HttpClient) {
        this.httpService = httpService;
    }

    public createOrder(entity: Order): Observable<Order> {
        let headers = new HttpHeaders().set('Content-type', 'application/json');
        return this.httpService.post<Order>('api/order', entity, {headers});
    }

    public getOrder(id: number): Observable<Order> {
        return this.httpService.get<Order>('api/order/' + id + '');
    }
    
}
```

##### Promise based service:
```typescript
export class OrderService {
    baseURL: URL;


    public constructor(baseURL: URL = new URL(window.document.URL)) {
        this.baseURL = baseURL;
    }

    public createOrder(entity: Order): Promise<Order> {
        const url = new URL('/api/order', this.baseURL);

        return fetch(url.toString(), {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        }).then(res => res.json());
    }

    public getOrder(id: number): Promise<Order> {
        const url = new URL('/api/order/' + id + '', this.baseURL);

        return fetch(url.toString(), {method: 'GET'}).then(res => res.json());
    }

}
```

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
   
# Basic Configuration
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
Java classes which describe payload model are generated to TypeScript interfaces. Java collections are converted into 
JavaScript arrays, Java Map is converted into object where key has a string type and value is converted Typescript Type
During model serialization to JSON, object mapper is applying some mappings(some of them are required for example in JSON, Date type must be
represented as string or number) based on some default configuration or dedicated annotations. Currently, Jackson Object
mapper and Gson Object mapper are supported. 

### Jackson Object mapper
JacksonObjectMapper allows to set fields, getters and setters visibility. Based on this Typescript 
interface fields are generated. Following Jackson annotations are supported :
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

### Gson Object mapper
Following Gson annotations are supported:
  + Expose - when used with `exluder.excludeFieldsWithoutExposeAnnotation()` only fields with this annotation are added to Typescript
  + Since - when used with `exluder..withVersion(...)` only fields witch matches version are added to Typescript, there is also added comment to Typescript field
  + Until - when used with `exluder..withVersion(...)` only fields witch matches version are added to Typescript, there is also added comment to Typescript field
  + SerializedName - changes field name in Typescript type

Gson object mapper is configured by Gson excluder so it gives all possibilities to filter classes and fields given by gson excluder.
Mapper can be also configured with `FieldNamingStrategy` to change fields names.
In Gson there is a problem with `@JsonAdapter` which could changes completely how object looks after serialization. This
problem could be divided in two cases:
+ when complex type is changed into primitive one for Example Date into number, such case could be solved by [Custom type mapping](#custom-type-mapping) When custom type naming is defined Java class is not converted into typescript type
+ when complex type is converted into Typescript type with some modifications, in that case do not define Custom type mapping, normal conversion will be applied and register [Conversion Listener](#conversion-listeners) to modify class as it is required

Gson doesn't support natively polymorphic types, in such case in root class there is required an additional field which will be a type denominator, such field could be also added by Conversion Listener 
To use Gson Object mapper you need to pass it to modelClassesConverter in the configuration


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
## Support for spring data in Spring REST controllers - since ver 1.2.2 
Parameters with type 'Pageable' from spring data, now are supported by adding extension to spring converter:
```java
    restClassesConverter.getConversionExtensionList().add(new SpringDataRestConversionExtension());
```
Data from pageable are sent to server and server response with Page object which contains Pageable, fields which can be set
on client side are not readonly, namely:
```typo3_typoscript
export interface Pageable {
    pageNumber: number;
    pageSize: number;
    sort?: Sort;
    readonly paged?: boolean;
    readonly offset?: number;
    readonly unpaged?: boolean;
}
```
before REST method call in Pageable object pageNumber and pageSize must be set and optionally sort. 
Others fields are calculated on the server and sent back to client in Page object

If parameter with type `Pageable` is marked with annotation `@PageableDefault`, parameter in TypeScript is optional 

## JaX-RS REST controllers converter - since ver 1.2.4
Since version 1.2.4 there is provided `JaxRsRestToTsConverter` which converts JAX-RS controllers into TypeScript services. 
It supports following JAX-RS annotations:
   + @GET
   + @POST
   + @PUT
   + @DELETE
   + @PATCH 
   + @Path 
   + @Produces 
   + @Consumes 
   + @PathParam 
   + @DefaultValue 
   
`JaxRsRestToTsConverter` converter can be used with existing implementation generators `Angular4ImplementationGenerator`
and `FetchBasedImplementationGenerator` so code could be generated for Angular as well as for frameworks where Promises are used. 
`JaxRsRestToTsConverter` usage example:
```java
    ClassNameMapper classNameMapper = new SubstringClassNameMapper("ResourceImpl", "Service");
    JaxRsRestToTsConverter jaxRsRestToTsConverter = new JaxRsRestToTsConverter(new Angular4ImplementationGenerator(), classNameMapper);
    tsGenerator.setRestClassesConverter(jaxRsRestToTsConverter);
``` 
Class `JaxRsGenerationTest` contains examples how to generate TypeScript code from JAX-RS REST endpoints     

## Java model classes to angular2-json-api  converter - since ver 1.2.4

[angular2-jsonapi](https://github.com/ghidoz/angular2-jsonapi) is an TypeScript
library which converts in web application incoming JSON into classes and classes into JSON.
Given advantage is that TypeScript interfaces doesn't exists in runtime in opposite to classes but to use classes, 
conversion is required which is done by `angular2-jsonapi` library, to enable this conversions some TypeScript
decorators are required on generated TypeScript model classes. There is provided java model classes converter 
`ModelClassesToTsAngular2JsonApiConverter` which adds following TypeScript decorators
   + JsonApiModelConfig - class level decorator
   + Attribute - set for simple fields
   + HasMany - set for collections
   + BelongsTo - set for fields with type which is model class 

First TypeScript class in the inheritance root has set base class to TypeScript `JsonApiModel` 
as it is required by `angular2-jsonapi` library

`JsonApiModelConfig` decorator  has parameter `type` which is type name, this field is generated
from TypeScript class name (name.toLowerCase()+"s") or is taken from Java annotation `JsonApiModelConfig`. To customize 
type name, Java model class must be annotated with `JsonApiModelConfig` annotation which is provided by module
```xml
    <dependency>
        <groupId>com.blue-veery</groupId>
        <artifactId>spring-rest2ts-angular2json-api</artifactId>
        <version>1.3.0</version>
    </dependency>
```
which needs to be included in Java project. 
Simple configuration:
```java
//set model class converter
    JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
    jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
    ModelClassesToTsAngular2JsonApiConverter modelClassesConverter = new ModelClassesToTsAngular2JsonApiConverter(jacksonObjectMapper);
    tsGenerator.setModelClassesConverter(modelClassesConverter);
```
`ModelClassesToTsAngular2JsonApiConverter` has also ability to generate model variable
which is a mapping from type name to TypeScript class, this mapping is required by `angular2-jsonapi` configuration object.
To have such config generated, generator configuration needs to create such variable, 
```java
    ModelClassesToTsAngular2JsonApiConverter modelClassesConverter = new ModelClassesToTsAngular2JsonApiConverter(jacksonObjectMapper);
    //models variable is optional, if not set it will not be generated, module selection is up to user decision
    JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = tsGenerator.getJavaPackageToTsModuleConverter();
    //there is selected TypeScript module where there will be generated class for ManufacturerDTO.class
    TSModule tsModuleForModelsVariable = javaPackageToTsModuleConverter.getTsModule(ManufacturerDTO.class);
    JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = tsGenerator.getJavaPackageToTsModuleConverter();
    TSModule tsModuleForModelsVariable = javaPackageToTsModuleConverter.getTsModule(ManufacturerDTO.class);
    modelClassesConverter.createModelsVariable("models", tsModuleForModelsVariable);
    tsGenerator.setModelClassesConverter(modelClassesConverter);
```
Configuration examples are in class Angular2JsonApiTest

<b style="color:red">This converter is an experimental version, generated code could 
contains some inconsistencies since we weren't able to find rules 
how to apply angular2-jsonapi decorators to fields, only examples,
base on which incorrect conclusions could be made!</b>
   
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

## Model serializers extensions : since ver 1.2.6
Since ver 1.2.6 there is a possibility to use a custom JSON serializers/deserializers in generated code. By default, there is used
`StandardJsonSerializerExtension` which is generating code based on standard JavaScript `JSON` object (`JSON.stringify` & `JSON.parse`).
`Json5ModelSerializerExtension` extension allows to use `JSON5` serializer. Following code snippet is showing how to configure generator:

```
    ImplementationGenerator implementationGenerator = new Angular4ImplementationGenerator();
    implementationGenerator.setSerializationExtension(new Json5ModelSerializerExtension());
    restClassesConverter = new SpringRestToTsConverter(implementationGenerator);
    tsGenerator.setRestClassesConverter(restClassesConverter);

    tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
``` 
To provide own JSON serializers/deserializers extension, user should implement `ModelSerializerExtension` interface  


## Nullable types
TypeScript has s great feature to warn about cases where value could be null. To use it TS compiler option must be set
`strictNullChecks` and proper fields in model set as nullable or proper parameters in methods which make REST calls. 
Generators support this by marking TS fields based on information taken from corresponding java classes. Generator is using 
`NullableTypesStrategy` with default implementation `DefaultNullableTypesStrategy`
which marks types as nullable if
  + field or method parameter type is wrapped in Java `java.util.Optional`
  + field or method parameter is marked with annotation `javax.annotation.Nullable`
  + field or method parameter has type which is Java wrapper for primitive types

Marks mean that field type is union of original field type and null; 
 
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
## TypeScript code formatting
Generated TypeScript code is not formatted. Code formatting from project to project
could have totally different requirements and it is suggested to use dedicated formatting library 
for this purpose like:
  + [prettier.io](https://prettier.io/docs/en/index.html) 
  + [typescript-formatter](https://github.com/vvakame/typescript-formatter) 
    

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
In most cases such situation is just a bug but there could be a situations in which this is required to handle TypeScript field
as an union of getter and setter type, in this order. Special case of this situation is when getter or setter is missing.
For missing setter field in TypeScript it is marked as readonly. For missing getter (for example we send password but do not read them from server)
TypeScript field is union of `undefined | <setter type>` because after read from server such fields will have value of undefined

### Java REST controllers overloaded methods
Java allows for overloaded methods what is not supported by TypeScript/JavaScript. To solve this problems if overloaded methods are met
in REST controller, generated method names in TypeScript are changed by appending in first round HTTP methods to TypeScript method name 
if they differ, if not URL path is appended splitted on `/` and joined with `_`

### Angular compilation issue for ambient modules d.ts
Angular for modules with typings, reports an error : `Module not found: Error: Can't resolve <module path>` 
We think that it is reported bug: https://github.com/angular/angular-cli/issues/4874 tsc compilation for generated modules 
works fine. To overcome this problem generator by default, generates only normal modules `*.ts`. To generate ambient modules
 following option must be set:
```java
Rest2tsGenerator.generateAmbientModules = true; 
``` 

## Unsupported mappings, coming soon...
  + multipart mapping `RequestPart`     
 
