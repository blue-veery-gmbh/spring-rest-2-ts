package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSInterface;

public class TSInterfaceReference extends TSParameterizedTypeReference<TSInterface> {
    public TSInterfaceReference(TSInterface referencedType) {
        super(referencedType);
    }
}
