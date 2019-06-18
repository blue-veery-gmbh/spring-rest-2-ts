package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class ProductDTO extends BaseDTO {
    private String name;
    private BigInteger price;
    private LocalDateTime expirationDate;
    private AddressDTO homeAddress;
}
