package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSModule;

public class TypeBasedJacksonJsConversion implements ConversionListener {
    private TSModule jacksonJSModule;
    protected TSDecorator jsonProperty;

    public TypeBasedJacksonJsConversion() {
        jacksonJSModule = new TSModule("jackson-js", null, true);
        jsonProperty = new TSDecorator(new TSFunction("JsonProperty", jacksonJSModule));
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        tsField.getTsDecoratorList().add(jsonProperty);
    }
}
