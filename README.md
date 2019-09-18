Purpose
spring-rest2ts-generator generates type script code based on spring mvc rest controllers and object model used in request and responses.
From model classes there are generated typescript interfaces and from REST controllers full working Angular services based on Observable API
or plain JS services based on Promises API. The main idea is that in Spring annotations describing REST endpoints there is enough information
to decompose HTTP request to call REST endpoint so there is enough information to generate TypeScript code to compose re quest from given parameters
which are aligned with Spring endpoint parameters. Such generated code reduce amount of hand written code on frontend and gives type safe calls to backend

Configuration
Due to greater flexibility spring-rest2ts-generator is configured by code any configuration files are nod needed. 
Here is the simplest generator configurator:


        Rest2tsGenerator tsGenerator = new Rest2tsGenerator();
        
        //set java type filters
        tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(BaseDTO.class));
        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        //set model class converter
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        //set rest class converter
        restClassesConverter = new SpringRestToTsConverter(new Angular4ImplementationGenerator());
        tsGenerator.setRestClassesConverter(restClassesConverter);

        //set java root packages from which class scanning will start
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.examples");
        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
       
Java Class filtering 
Generator need two filters to select java classes for which TypeScript types will be generated, these filters 
allows to skip Java classes in given packages for which TypeScript generation is not needed. By default Rest2tsGenerator
for model classes and rest controllers has RejectJavaTypeFilter which rejects all classes in given java packages,
such filter could be used to skip generation for REST controllers when only Typescript types are need for Java model classes
There are following filters which allows to build complex conditions:
    
    ExtendsJavaTypeFilter
    HasAnnotationJavaTypeFilter
    JavaTypePackageFilter
    ContainsSubStringJavaTypeFilter
    RegexpJavaTypeFilter
    RejectJavaTypeFilter
    OrFilterOperator
    AndFilterOperator
    NotFilterOperator
    
Model class converter
Java classes which describe payload model are generated to TypeScript interfaces. During model serialization to JSON, 
object mapper is applying some mapping some of them are required for example in JSON there in type for Date so it must be
represented as string or number, based on some default configuration or dedicated annotations. Currently Jackson Object
mapper is supported. JacksonObjectMapper allows to set fields, getters and setters visibility based on this Typescript 
interface fields are generated. From jackson there are supported following annotations  :
    JsonTypeInfo - TS property is added if type info is serialized to property
    JsonAutoDetect - overrides default visibility 
    JsonIgnoreType - properties which have type marked as ignored are skipped 
    JsonIgnoreProperties - ignores listed properties
    JsonIgnore - ignores marked property
    JsonBackReference - ignores TS property
    JsonProperty - changes property name, based on access marks TS property as readonly
    JsonSetter - changes property name
    JsonGetter - changes property name
    JsonUnwrapped - add fields from property type to the current TS interface
    JsonValue
    JsonFormat - changes TS property type, add to TS comment pattern if given
    JacksonInject - marks TS property as readonly
    JsonRawValue - changes TS property type to any
Java collections are converted into arrays, Java Map is converted into object where key has a string type and value is converted 
Typescript Type

Rest classes converter
From REST classes there is generated working implementation in TypeScript which based on Spring annotations builds complete 
HTTP request. Such implementation depends also used JS framework, there is different approach to HTTP calls in Angular and React
so SpringRestToTsConverter requires instance of ImplementationGenerator. There are available two such implementation generators
    Angular4ImplementationGenerator - generates valid Angular services with methods which corresponds to REST endpoints  
    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
There are supported following Spring annotations:
    RequestMapping
    GetMapping 
    PostMapping
    PutMapping 
    DeleteMapping
    PatchMapping
    PathVariable
    RequestParam
    RequestBody
     

    

    
    
    
    
    
    
    
    





