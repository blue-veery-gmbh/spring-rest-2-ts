package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class AddressDTO extends BaseDTO {

    public String street;
    public String zipCode;
    public Integer buildingNumber;
    private String buildingNumberExtension;

}

