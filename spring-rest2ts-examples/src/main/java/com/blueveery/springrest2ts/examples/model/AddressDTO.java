package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class AddressDTO extends BaseDTO {

    public String street;
    public String zipCode;
    public Integer buildingNumber;
    private String buildingNumberExtension;


    public AddressDTO(String street, String zipCode, Integer buildingNumber, String buildingNumberExtension) {
        this.street = street;
        this.zipCode = zipCode;
        this.buildingNumber = buildingNumber;
        this.buildingNumberExtension = buildingNumberExtension;
    }

}