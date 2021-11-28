package com.blueveery.springrest2ts.converters.tests.model;

import javax.annotation.Nullable;

public class Product {
    public transient String tempName = "phone";

    public String name = "phone";

    public int productionYear = 2021;

    @Nullable
    int nullableField;

    Integer intWrapperField;
}

