package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.examples.model.enums.OrderPaymentStatus;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO extends BaseDTO {

    private PersonDTO buyer;
    private AddressDTO deliveryAddressDTO;
    private List<ProductDTO> productList = new ArrayList<>();
    private OrderPaymentStatus orderPaymentStatus;

}
