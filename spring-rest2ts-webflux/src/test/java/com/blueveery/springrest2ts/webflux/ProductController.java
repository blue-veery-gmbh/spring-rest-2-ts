package com.blueveery.springrest2ts.webflux;

import com.blueveery.springrest2ts.tests.model.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/product")
public class ProductController {

  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public Mono<Product> get(String id) {
    return Mono.just(new Product());
  }

  @RequestMapping(method = RequestMethod.GET)
  public Flux<Product> getAll() {
    return Flux.just(new Product());
  }
}