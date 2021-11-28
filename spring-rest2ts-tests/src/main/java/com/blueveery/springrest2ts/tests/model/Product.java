package com.blueveery.springrest2ts.tests.model;

import javax.annotation.Nullable;
import java.util.List;

public class Product {
    public transient String tempName = "phone";

    public String name = "phone";

    public int productionYear = 2021;

    @Nullable
    int nullableField;

    Integer intWrapperField;

    Keyboard keyboard;

    List<ExtendedKeyboard> extendedKeyboards;
}

