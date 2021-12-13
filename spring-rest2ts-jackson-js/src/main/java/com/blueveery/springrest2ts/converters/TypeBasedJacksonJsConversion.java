package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;

public class TypeBasedJacksonJsConversion implements ConversionListener {
    protected TSModule jacksonJSModule;
    protected TSDecorator jsonProperty;
    protected final TSFunction jsonClassTypeFunction;

    public TypeBasedJacksonJsConversion() {
        jacksonJSModule = new TSModule("jackson-js", null, true);
        jsonProperty = new TSDecorator(new TSFunction("JsonProperty", jacksonJSModule));
        jsonClassTypeFunction = new TSFunction("JsonClassType", jacksonJSModule);
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        tsField.getTsDecoratorList().add(jsonProperty);

        addJsonClassTypeDecorator(tsField);
    }

    private void addJsonClassTypeDecorator(TSField tsField) {
        TSJsonLiteral classTypeLiteral = new TSJsonLiteral();
        TSType type = TypeMapper.getTypeObjectTypeVersion(tsField.getType());
        classTypeLiteral.getFieldMap().put("type",
                new TSArrowFunctionLiteral(
                        new TSLiteralArray(
                                new TSLiteral("", type, type.getName())
                        )
                )
        );
        TSDecorator jsonClassTypeDecorator = new TSDecorator(jsonClassTypeFunction);
        jsonClassTypeDecorator.getTsLiteralList().add(classTypeLiteral);
        tsField.getTsDecoratorList().add(jsonClassTypeDecorator);
    }
}
