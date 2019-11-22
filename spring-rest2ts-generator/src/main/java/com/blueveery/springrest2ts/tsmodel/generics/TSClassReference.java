package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSClass;

public class TSClassReference extends TSParameterizedTypeReference<TSClass> {
    public TSClassReference(TSClass referencedType) {
        super(referencedType);
    }
}
