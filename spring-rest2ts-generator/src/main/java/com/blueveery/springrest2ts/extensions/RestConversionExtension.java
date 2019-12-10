package com.blueveery.springrest2ts.extensions;


import com.blueveery.springrest2ts.tsmodel.TSParameter;

public interface RestConversionExtension extends ConversionExtension{
    boolean isMappedRestParam(Class aClass);
    boolean isMappedRestParam(TSParameter tsParameter);

    default ModelConversionExtension getModelConversionExtension(){
        return null;
    }

    String generateImplementation(TSParameter tsParameter, String pathParamsList, String queryParamsList, String headerParamsList);
}
