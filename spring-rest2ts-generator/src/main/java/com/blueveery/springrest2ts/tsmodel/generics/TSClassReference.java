package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.util.List;

public class TSClassReference extends TSParameterizedTypeReference<TSClass> {
    public TSClassReference(TSClass referencedType, List<TSType> actualParameterList) {
        super(referencedType, actualParameterList);
    }

    public TSClassReference(TSClass referencedType, TSType actualParameter) {
        super(referencedType, actualParameter);
    }
}
