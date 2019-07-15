package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
public class ProductDTO extends BaseDTO {
    public String name;
    public BigInteger price;
    public String barcode;
    public LocalDateTime expirationDate;
    public ManufacturerDTO manufacturer;
    public Map<String, String> tags;

    @JsonManagedReference
    public CategoryDTO categoryDTO;

    @JsonRawValue
    public Map<UUID, String> translationsMap;

}

