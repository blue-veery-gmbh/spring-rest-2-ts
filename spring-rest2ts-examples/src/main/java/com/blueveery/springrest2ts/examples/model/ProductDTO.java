package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
public class ProductDTO extends BaseDTO {

    private String name = "p";
    private BigInteger price;
    private String barcode;
    private LocalDateTime expirationDate;
    private ManufacturerDTO manufacturer;
    private Map<String, String> tags;

//    private String blabla;

    @JsonManagedReference
    public CategoryDTO categoryDTO;

    @JsonRawValue
    public Map<UUID, String> translationsMap;

    public String getBarcode() {
        return barcode;
    }

    @JsonProperty("blabla")
    public String getName() {
        return name;
    }


    @JsonIgnore
    @JsonSetter(value = "blabla")
    public void name(String name) {
        this.name = name;
    }

    //    public String getBarcode() {
//        return barcode;
//    }
}

