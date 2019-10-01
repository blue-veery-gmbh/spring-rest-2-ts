package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/product-overloaded")
public class OverloadedMethodsCtrl implements GetObjectCtrl<CategoryDTO.ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createProduct(@RequestBody CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = "/product/create/{id}/with-update")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createProduct(@RequestBody CategoryDTO.ProductDTO entity, @RequestParam boolean update) {
        return entity;
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = "/product/create/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createOrder(@RequestBody CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = "/product/create/fake")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createOrder(@RequestBody CategoryDTO.ProductDTO entity, @RequestParam boolean update) {
        return entity;
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"text/plain"})
    @ResponseBody
    public long countProducts() {
        return 0;
    }
}
