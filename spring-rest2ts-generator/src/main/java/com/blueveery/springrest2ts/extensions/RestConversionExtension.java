package com.blueveery.springrest2ts.extensions;


public interface RestConversionExtension extends ConversionExtension{
    boolean isMappedRestParam(Class aClass);
    default ModelConversionExtension getModelConversionExtension(){
        return null;
    }
}
