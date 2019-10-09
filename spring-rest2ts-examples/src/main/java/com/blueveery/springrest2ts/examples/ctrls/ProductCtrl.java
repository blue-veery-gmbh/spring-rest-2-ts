package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/product")
public class ProductCtrl implements GetObjectCtrl<CategoryDTO.ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createOrder(@RequestBody CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<CategoryDTO.ProductDTO>> getProducts(@RequestParam(value = "price") BigInteger price,
                                                                    @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                    @RequestParam(value = "ascending", required = false, defaultValue = "true") Boolean ascending,
                                                                    @RequestParam(value = "pageNumber") int pageNumber,
                                                                    @RequestParam Optional<Integer> pageSize) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"text/plain"})
    @ResponseBody
    public long countProducts() {
        return 0;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") int id) {
        return ResponseEntity.noContent().build();
    }
}
