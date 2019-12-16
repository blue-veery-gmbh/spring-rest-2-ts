package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreType;


public class ManufacturerDTO extends BaseDTO {

    private String name;
    private String shortName;
    private AddressDTO headquartersAddress;
}
