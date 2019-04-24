Spring-rest-2-ts is a code generator which generates from Jackson/Gson and Spring REST annotations, Typescript REST API typed HTTP client implemntation classes and interfaces for Java domain class

Spring-rest-2-ts has been created at blue veery gmBh as an internal project, there two others typescript generator but they are mainly focused on model classes and for REST controllers they are generating only interfaces and we wanted to have generated classes with the implementation which are able to create HTTP request based on the information contained in Spring MVC annotations. During 4 weeks we created this generator and tested in two large project, in both generated code was on the level 16% of entire Typescript code in the project.




Features summary

Entity/Data model/ Data transfer objects features:

    interfaces generated based on Jackson or Gson annotations

    typescript module per Java package or Java class

    Java fields with primitive Java Wrappers types are generated as optional typescript field

    field with type Optional<T> is mapped as optional typescript field

    custom type mappings, sometimes JSON serializer changes field type e.g.. Java Date class could be serialized as string or number of milliseconds, in this situation mapping could be specified by custom mapping

    Java Enums are generated as Typescript enums, here serializer must be configured to serialize enums as its ordinal values

Supported Jackson annotations:

    JsonAutoDetect to filter properties in Typescript type

    JsonIgnore

    JsonIgnoreType

    JsonIgnoreProperties – if allowGetters is set to true, property is included but marked as readonly

    JsonValue – if field type has method with JsonValue annotation, this method return type is mapped to Typescript type and set as Typescript field type

    JsonFormat - JsonFormat.Shape enum is used to set Typescript field type

    JsonUnwrapped

    JacksonInject - if annotation is present, corresponding Typescript field is marked as readonly

    JsonSetter - todo

    JsonGetter - todo

    JsonRawValue - if annotation is present, corresponding Typescript field type is set to any

    JsonRootName - todo

    JsonCreator - todo

    JsonManagedReference/JsonBackReference - field with JsonBackReference annotation is ignored

    JsonTypeInfo - when include is set to PROPERTY additional field is added for type information, support for values WRAPPER_OBJECT, WRAPPER_ARRAY could be considered
Supported Gson annotations: - todo

Supported for Spring MVC :

    typed http client implementation based on plain Typescript class and Promises API

    typed http client implementation based on Angular services and Observable API

    Supported Spring MVC annotations:

        RequestMapping – request path is taken, request method, media type for serialization/deserialization

        PathVariable – parameter mapped with this annotation in REST controller class is mapped as parameter in typescript, required field from this annotation is mapped as optional method parameter. Parameter is used to create request path where path variable is changed to value given during typescript method call

        RequestParam - parameter mapped with this annotation in REST controller class is mapped as parameter in typescript, required field from this annotation is mapped as optional method parameter. Parameter is used to create request param

        RequestBody – parmater mapped with this annotation is used create request body after serialization

