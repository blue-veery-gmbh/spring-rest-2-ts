package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.*;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CUSTOM)
public class ProductDTO extends BaseDTO {

    public int name = 1;
    private BigInteger price;
    private String barcode;
    private LocalDateTime expirationDate;
    private ManufacturerDTO manufacturer;
    private Map<String, String> tags;

//    private String blabla;

//    @JsonManagedReference
    public CategoryDTO categoryDTO;

    @JsonRawValue
    public Map<UUID, String> translationsMap;

//    public String getBarcode() {
//        return barcode;
//    }


    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    //    @JsonProperty("blabla")
    public int getName() {
        return name;
    }


    @JsonSetter(value = "secondName")
    public void name( String name) {

    }

}

