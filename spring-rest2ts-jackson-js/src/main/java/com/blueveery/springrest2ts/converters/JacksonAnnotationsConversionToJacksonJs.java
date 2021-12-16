package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSField;

public class JacksonAnnotationsConversionToJacksonJs extends TypeBasedConversionToJacksonJs{
    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        super.tsFieldCreated(property, tsField);
    }
}
