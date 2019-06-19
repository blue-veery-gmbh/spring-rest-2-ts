package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.math.BigInteger;
import java.time.LocalDateTime;


public class ProductDTO extends BaseDTO {
    public String name;
    public BigInteger price;
    public LocalDateTime expirationDate;
    public ManufacturerDTO manufacturer;
}

