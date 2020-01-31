package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.util.List;

public class TSInterfaceReference extends TSParameterizedTypeReference<TSInterface> {
    public TSInterfaceReference(TSInterface referencedType, List<TSType> actualParameterList) {
        super(referencedType, actualParameterList);
    }

    public TSInterfaceReference(TSInterface referencedType, TSType actualParameter) {
        super(referencedType, actualParameter);
    }

}
