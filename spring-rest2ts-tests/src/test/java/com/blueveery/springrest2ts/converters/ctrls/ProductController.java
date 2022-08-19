package com.blueveery.springrest2ts.converters.ctrls;

import com.blueveery.springrest2ts.converters.enums.ProductType;
import com.blueveery.springrest2ts.converters.enums.SingleResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/product")
public class ProductController {

  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public SingleResult<ProductType> getProductType(String id) {
    return new SingleResult<>();
  }
}