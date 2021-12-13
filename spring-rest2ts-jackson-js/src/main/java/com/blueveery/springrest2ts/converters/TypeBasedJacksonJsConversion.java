package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.blueveery.springrest2ts.tsmodel.TSUnion;

import java.util.List;
import java.util.stream.Collectors;

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
        classTypeLiteral.getFieldMap().put("type",
                new TSArrowFunctionLiteral(convertToTypeLiteral(tsField.getType()))
        );
        TSDecorator jsonClassTypeDecorator = new TSDecorator(jsonClassTypeFunction);
        jsonClassTypeDecorator.getTsLiteralList().add(classTypeLiteral);
        tsField.getTsDecoratorList().add(jsonClassTypeDecorator);
    }

    private ILiteral convertToTypeLiteral(TSType type) {
        TSType sourceType = type;
        if (type instanceof TSUnion) {
            TSUnion tsUnion = (TSUnion) type;
            List<TSElement> typeList = tsUnion.getJoinedTsElementList().stream()
                    .filter(t -> t != TypeMapper.tsNull && t != TypeMapper.tsUndefined)
                    .collect(Collectors.toList());
            if (typeList.size() > 1) {
                String typeListString = typeList.stream().map(TSElement::getName).collect(Collectors.joining(", "));
                throw new IllegalStateException(
                        "TSUnion type should contain one non-null type to be able to define jackson-js mappings! type list: " + typeListString
                );
            }

            sourceType = typeList.size() == 1 ? (TSType) typeList.get(0) : TypeMapper.tsObject;
        }

        if (sourceType instanceof TSArray) {
            TSArray tsArray = (TSArray) sourceType;
            return new TSLiteralArray(new TSTypeLiteral(tsArray), convertToTypeLiteral(tsArray.getElementType()));
        }

        return new TSLiteralArray(new TSTypeLiteral(TypeMapper.getTypeObjectTypeVersion(sourceType)));
    }
}
