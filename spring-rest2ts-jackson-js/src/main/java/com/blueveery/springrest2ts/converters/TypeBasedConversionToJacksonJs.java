package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;

import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.convertToTypeLiteral;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.jacksonJSModule;
import static com.blueveery.springrest2ts.jacksonjs.JacksonJsTypeTransformer.wrapIntoTSLiteralArray;

public class TypeBasedConversionToJacksonJs implements ConversionListener {

    protected final TSFunction jsonPropertyFunction;
    protected final TSFunction jsonClassTypeFunction;
    private TSDecorator jsonProperty;

    public TypeBasedConversionToJacksonJs() {
        jsonPropertyFunction = new TSFunction("JsonProperty", jacksonJSModule);
        jsonProperty = new TSDecorator(jsonPropertyFunction);
        jsonClassTypeFunction = new TSFunction("JsonClassType", jacksonJSModule);
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        if (tsField.getOwner() instanceof TSClass) {
            tsField.getTsDecoratorList().add(jsonProperty);
            tsField.getOwner().addScopedTypeUsage(jsonProperty.getTsFunction());

            addJsonClassTypeDecorator(tsField);
        }
    }

    private void addJsonClassTypeDecorator(TSField tsField) {
        TSJsonLiteral classTypeLiteral = new TSJsonLiteral();
        ILiteral returnValue = wrapIntoTSLiteralArray(convertToTypeLiteral(tsField.getType()));
        classTypeLiteral.getFieldMap().put("type",new TSArrowFunctionLiteral(returnValue));
        TSDecorator jsonClassTypeDecorator = new TSDecorator(jsonClassTypeFunction);
        jsonClassTypeDecorator.getTsLiteralList().add(classTypeLiteral);
        tsField.getTsDecoratorList().add(jsonClassTypeDecorator);
        tsField.getOwner().addScopedTypeUsage(jsonClassTypeDecorator.getTsFunction());
    }
}
