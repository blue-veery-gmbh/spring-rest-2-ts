# jaxrs-demo-1.x [![Build Status][travis-img]][travis]

JAX-RS 1.x demo using Jersey. JAX-RS is the Java™ API for RESTful Web Services ([JSR 311][jsr-311]).
Here's a demonstration using Jersey—the reference implementation of JAX-RS 1.x.

## Prerequisite

- Java 8 (Java 9+ won't work)
- Maven 3
- (optional) `jq` for JSON formatting

## Installation

Build application `shop` and run the server:

    $ mvn clean install
    $ mvn exec:java -f shop-server/pom.xml

The server is now running on <http://localhost:8080/api/>.

## Demo

```
$ curl -s http://localhost:8080/api/products/1 | jq
{
  "id": "1",
  "name": "foo"
}
```

## Serialization

Serialization is the conversion of the state of an object into a byte stream;
deserialization does the opposite.

### Without Serialization Provider

Without serialization provider, a web resource method returns a complete HTTP
`Response`, which contains HTTP headers and HTTP entity:

```java
@GET
@Path("{id}")
@Produces(MediaType.APPLICATION_JSON)
Response getProduct(@PathParam("id") String id);
```

### With Serialization Provider

With serialization provider, the serialization is delegated. The new
organization is as follows:

- Web resource returns a POJO.
- Serialization provider is registered in application.
- POJO declares annotations for serialization mapping.

A web resource method returns a Java object, e.g. `Product`:

```java
@GET
@Path("{id}")
@Produces(MediaType.APPLICATION_JSON)
Product getProduct(@PathParam("id") String id);
```

The serialization is handled to the provider, registered to the
application. For example, the JSON serialization is delegated to
`JacksonJsonProvider`, which is a third party library from Jackson:

```java
public class ShopApplication extends Application {
  ...

  @Override
  public Set<Object> getSingletons() {
    Set<Object> set = new HashSet<>();
    set.add(new JacksonJsonProvider());
    return set;
  }
}
```

Now let's take a look on POJO `Product`. JSON annotations are needed for 2
things:

- Serialization (`@JsonProperty`)
- Deserialization (`@JsonCreator`)

Declare `@JsonProperty` on each class member or class method, which will be
used for JSON serialization. The value "id" in `@JsonProperty` annotation seems
duplicate with the class member `id`. However, this is more declarative. Since
JSON fields are shared publicly with other applications as API, it's important
to make it explicit. It also keeps the consistency when class member is
renamed.

Declare `@JsonCreator` on constructor, so that Jackson understand how the POJO
can be constructed from a serialized string. This is not the only way to do it,
see <http://www.baeldung.com/jackson-annotations> for more examples.

```java
public class Product {

  @JsonProperty("id")
  private final String id;

  @JsonProperty("name")
  private final String name;

  @JsonCreator
  public Product(@JsonProperty("id") String id, @JsonProperty("name") String name) {
    this.id = id;
    this.name = name;
  }
}
```

## Exception Mapping

### Exception Mapping Server Side

Implement class `ExceptionMapper` to handle the customized exception mapping.

```java
public class ShopExceptionMapper implements ExceptionMapper<ShopException> {
  @Override
  public Response toResponse(ShopException ex) {
    ...
  }
}
```

Register the mapper as a singleton in your application:

```java
public class ShopApplication extends Application {
  ...

  @Override
  public Set<Object> getSingletons() {
    Set<Object> set = new HashSet<>();
    set.add(new ShopExceptionMapper());
    ...
    return set;
  }
}
```

### Exception Mapping Client Side

Use a proxy-client approach to map 4xx and 5xx HTTP responses into exception:

```java
WebResource wr = Client.create(cc).resource(Main.BASE_URI.resolve("products"));
wr.addFilter(new ClientFilter() {
  @Override
  public ClientResponse handle(ClientRequest request) {
    ClientResponse response = getNext().handle(request);
    if (response.getStatus() >= 400) {
      ShopExceptionData data = response.getEntity(ShopExceptionData.class);
      throw new ShopException(response.getStatus(), data);
    }
    return response;
  }
});
```

## References

- [Baeldung: Jackson Annotation Examples][jackson-annotations]

[jsr-311]: https://jcp.org/en/jsr/detail?id=311
[jackson-annotations]: http://www.baeldung.com/jackson-annotations
[travis]: https://travis-ci.org/mincong-h/jaxrs-1.x-demo
[travis-img]: https://travis-ci.org/mincong-h/jaxrs-1.x-demo.svg?branch=master
