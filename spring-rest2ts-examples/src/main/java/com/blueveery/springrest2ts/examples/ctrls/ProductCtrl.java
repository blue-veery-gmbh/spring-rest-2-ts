package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/product")
public class ProductCtrl implements GetObjectCtrl<ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createOrder(@RequestBody ProductDTO entity) {
        return entity;
    }

    @GetMapping(produces = {"application/json"})
    public List<ProductDTO> getProducts(@RequestParam(value = "price") BigInteger price,
                                        @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                        @RequestParam(value = "ascending", required = false, defaultValue = "true") Boolean ascending,
                                        @RequestParam(value = "pageNumber") int pageNumber,
                                        @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return Collections.emptyList();
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"text/plain"})
    @ResponseBody
    public long countProducts() {
        return 0;
    }
}
