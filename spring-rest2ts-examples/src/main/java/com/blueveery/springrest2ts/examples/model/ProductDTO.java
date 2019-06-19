package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.time.LocalDateTime;

@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
public class ProductDTO extends BaseDTO {
    public String name;
    public BigInteger price;
    public String barcode;
    public LocalDateTime expirationDate;
    public ManufacturerDTO manufacturer;
}

