package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

public class GenericReference<T extends BaseDTO> extends BaseDTO{
    public T reference;
}

class ManufacturerReferenceDTO extends BaseDTO{
    GenericReference<ManufacturerDTO> manufacturerReference;
}



