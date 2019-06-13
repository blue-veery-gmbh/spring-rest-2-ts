package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO extends BaseDTO {

    private List<ProductDTO> productList = new ArrayList<>();
    private AddressDTO deliveryAddressDTO;
    private PersonDTO buyer;

}
