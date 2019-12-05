package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.converters.ObjectMapper;

import java.util.Collections;
import java.util.Map;

public interface ModelConversionExtension extends ConversionExtension{
    default Map<String, ObjectMapper> getObjectMapperMap(){
        return Collections.emptyMap();
    }
}
