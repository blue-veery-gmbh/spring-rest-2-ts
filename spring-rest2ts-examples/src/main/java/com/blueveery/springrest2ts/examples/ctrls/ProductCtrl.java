package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("api/product")
public class ProductCtrl implements GetObjectCtrl<ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createOrder(@RequestBody ProductDTO entity) {
        return entity;
    }

    @GetMapping(produces = {"application/json"})
    public List<ProductDTO> getProducts(@RequestParam(value = "price") BigDecimal price,
                                        @RequestParam(value = "pageNumber") int pageNumber,
                                        @RequestParam(value = "pageSize") int pageSize) {
        return Collections.emptyList();
    }
}
