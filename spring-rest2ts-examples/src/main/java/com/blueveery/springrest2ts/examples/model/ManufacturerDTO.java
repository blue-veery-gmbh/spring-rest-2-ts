package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.angular2jsonapi.JsonApiModelConfig;
import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

@JsonApiModelConfig(type="manufactures")
public class ManufacturerDTO extends BaseDTO {

    private String name;
    private String shortName;
    private AddressDTO headquartersAddress;
}
