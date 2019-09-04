package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/product")
public class OverloadedMethodsCtrl implements GetObjectCtrl<ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO entity) {
        return entity;
    }

    @PutMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO entity, @RequestParam boolean update) {
        return entity;
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = "/product/create/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createOrder(@RequestBody ProductDTO entity) {
        return entity;
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = "/product/create/fake")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createOrder(@RequestBody ProductDTO entity, @RequestParam boolean update) {
        return entity;
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"text/plain"})
    @ResponseBody
    public long countProducts() {
        return 0;
    }
}
