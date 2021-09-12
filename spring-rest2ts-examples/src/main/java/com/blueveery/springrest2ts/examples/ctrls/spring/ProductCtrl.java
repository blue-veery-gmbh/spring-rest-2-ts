package com.blueveery.springrest2ts.examples.ctrls.spring;

import com.blueveery.springrest2ts.examples.ctrls.spring.core.BaseCtrlImpl;
import com.blueveery.springrest2ts.examples.ctrls.spring.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/product")
public class ProductCtrl extends BaseCtrlImpl<CategoryDTO.ProductDTO> implements GetObjectCtrl<CategoryDTO.ProductDTO> {

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.ProductDTO createProduct(@RequestBody CategoryDTO.ProductDTO entity) {
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

    @RequestMapping(path = "/count", method = RequestMethod.POST, produces = {"text/plain"})
    @ResponseBody
    public long countProductsLike(@RequestBody  CategoryDTO.ProductDTO product) {
        return 0;
    }
}
