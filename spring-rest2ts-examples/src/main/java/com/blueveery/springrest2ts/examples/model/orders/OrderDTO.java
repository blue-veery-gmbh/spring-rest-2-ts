package com.blueveery.springrest2ts.examples.model.orders;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO extends BaseDTO {
    List<ProductDTO> productList = new ArrayList<>();
    Address deliveryAddress;
}
