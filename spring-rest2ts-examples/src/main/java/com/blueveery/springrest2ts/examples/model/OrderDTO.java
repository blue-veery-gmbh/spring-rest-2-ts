package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.blueveery.springrest2ts.examples.model.enums.OrderDeliveryStatus;
import com.blueveery.springrest2ts.examples.model.enums.OrderPaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO extends BaseDTO {

    private PersonDTO buyer;
    private AddressDTO deliveryAddressDTO;
    private List<ProductDTO> productList = new ArrayList<>();
    private OrderPaymentStatus orderPaymentStatus;
    private OrderDeliveryStatus orderDeliveryStatus;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss"
    )
    private LocalDateTime orderTimestamp;
}
