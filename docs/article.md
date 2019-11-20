# How to generate TypeScript code from Spring MVC REST Controllers 
I had been for eighteen years backend developer, with dawn of single page applications my cooperation with web developers 
began to be closer I looked with interest at theirs jobs and considered to learn from them to be full stack developer. 
I noticed they create in A TypeScript model and services which reflected our backend model and REST services. It was a tedious
job but after some time they finally reflected what we had on backend but their smile was immediately broken by common thing 
in software development... a CHANGE due to some changes in business requirements backend services had been changed which caused that 
frontend developers were forced to reanalyse backend service to find a change and make refactoring in frontend application.

After some time I started my adventure with web development and I at this moment I came with an idea that all this stuff 
could be generated from type information in java. I started research for TypeScript generator which will be able to 
generate model and services in TypeScript based on REST interfaces. It turned out that there are few libraries which 
are doing such thing but none of them covered all of our needs, in short available generic solution doesn't covered our special needs
which were
  + support for JavaBean convention      
  + support for FasterXML/Jackson annotation      
  + support for Spring framework - which means generation of TypeScript services which are able to call REST API developed in Spring       
  + generated services aligned with Angular and ReactJS specific requirements (Observable or Promises API)      

At blue veery gmbh  we decided to launch small off hours project which gives us functionality which we wanted. 
Project succeed, we tested it in few of our projects were web application was based on Angular and React,
 average generated code was on level of 20% percent of web application codebase but in terms of saved work on changes and tests
 it is much more and hard to estimate. Having such promising results our company has decided to open it to the open source.
 In this short article I would like to introduce how You can REST with our spring-rest-2-ts TypeScript generator if Your development setup
  is close to ours:  Spring framework on backend, on fronted Angular Or React    
  
# Examples
To get imagination what spring-rest2ts generator can do let's create simple model and rest controller in Java and we will show 
what will be generated in TypeScript
```java
public class BaseDTO {
    private int id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date updateTimeStamp;
 }

public class OrderDTO extends BaseDTO {
    private double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime orderTimestamp;
}
```

Spring REST controller:
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
In TypeScript for DTO classes we get two interfaces with mapped inheritance and each field mapped to respective TypeScript
types: 
```typescript

export interface Base {
    id: number;
    updateTimeStamp: number;
}

export interface Order extends Base {
    price: number;
    /**
     *    pattern : dd-MM-yyyy hh:mm:ss
     */
    orderTimestamp: string;
}
```
as we see if field has Jackson annotation it is taken into account if not transformation is based on Java types to TypeScript
mapping. There is support for Type names mapping In Java we see that there is `OrderDTO` by providing proper name mapper which 
cuts off postfix `DTO` and we get type `Order`
##### Observable based service:
Mapping of model classes is quite easy, more interesting is a mapping of Spring REST controllers for which in TypeScript there is 
generated implementation to call endpoints, such approach hides under method names paths and parameters names so code will 
be resistant to changes on the backend. What is more important we transform return types to selected web frameworks, 
for Angular 2+ there is generated valid Angular service ready to use for injection:
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
`OrderService` is generated for `OrderCtrl` here type name was also transformed by type name mapper. If REST API is not 
available on the same host as web application there is possibility to configure baseURL which could be a path prefix o 
entire host reference  

##### Promise based service:
For web frameworks which are using `Promise` API generator by proper configuration is also able to generate service class:  
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
    modelClassesConverter.setClassNameMapper(new SubstringClassNameMapper("DTO", ""));
    tsGenerator.setModelClassesConverter(modelClassesConverter);
    
    // Spring REST controllers converter
    restClassesConverter = new SpringRestToTsConverter(new Angular4ImplementationGenerator());
    restClassesConverter.setClassNameMapper(new SubstringClassNameMapper("Ctrl", "Service"));
    tsGenerator.setRestClassesConverter(restClassesConverter);
    
    // set of java root packages for class scanning
    javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.examples");
    tsGenerator.generate(javaPackageSet, Paths.get("../target/ts-code"));
```

For more information please check project page on github https://github.com/blue-veery-gmbh/spring-rest-2-ts



    


  
  
  
  

      
