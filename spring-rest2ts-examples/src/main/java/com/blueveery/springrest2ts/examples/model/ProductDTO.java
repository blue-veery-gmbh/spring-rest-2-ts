package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.*;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

//@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProductDTO extends BaseDTO {

    public int name = 1;
    private BigInteger price;
    private String barcode;
    public URI websiteURI;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date expirationDate;
    private ManufacturerDTO manufacturer;
    private Map<String, String> tags;

//    @JsonManagedReference
    public CategoryDTO categoryDTO;

    @JsonRawValue
    public Map<UUID, String> translationsMap;

    public String getBarcode() {
        return barcode;
    }


    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getName() {
        return name;
    }


    @JsonSetter(value = "secondName")
    public void name(@Nullable String name) {
    }

}

