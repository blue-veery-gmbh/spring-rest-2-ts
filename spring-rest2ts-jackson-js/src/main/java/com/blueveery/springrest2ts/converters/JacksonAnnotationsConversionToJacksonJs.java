package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class JacksonAnnotationsConversionToJacksonJs extends TypeBasedConversionToJacksonJs {

    protected final TSFunction jsonTypeInfoFunction;
    protected final TSEnum jsonTypeInfoIdEnum;

    public JacksonAnnotationsConversionToJacksonJs() {
        jsonTypeInfoFunction = new TSFunction("JsonTypeInfo", jacksonJSModule);
        jsonTypeInfoIdEnum = new TSEnum("JsonTypeInfoId", jacksonJSModule);
        jsonTypeInfoIdEnum.add("NAME");
    }

    @Override
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        TSClass tsClass = (TSClass) tsScopedElement;
        super.tsScopedTypeCreated(javaType, tsClass);

        JsonTypeInfo jsonTypeInfo = (JsonTypeInfo) javaType.getAnnotation(JsonTypeInfo.class);
        if (jsonTypeInfo != null) {
            addJacksonAnnotation(tsClass, jsonTypeInfo);
        }
    }

    private void addJacksonAnnotation(TSClass tsClass, JsonTypeInfo jsonTypeInfo) {
        TSDecorator jsonTypeInfoDecorator = new TSDecorator(jsonTypeInfoFunction);
        TSJsonLiteral jsonTypeInfoLiteral = new TSJsonLiteral();
        jsonTypeInfoDecorator.getTsLiteralList().add(jsonTypeInfoLiteral);
        tsClass.addScopedTypeUsage(jsonTypeInfoFunction);
        jsonTypeInfoLiteral.getFieldMap().put("use", new TSLiteral("", TypeMapper.tsAny, "JsonTypeInfoId.NAME"));
        tsClass.addScopedTypeUsage(jsonTypeInfoIdEnum);
        tsClass.getTsDecoratorList().add(jsonTypeInfoDecorator);
    }
}
