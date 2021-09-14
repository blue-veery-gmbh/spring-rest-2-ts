package com.blueveery.springrest2ts.converters;

import com.google.gson.annotations.Expose;

class Product {
    public transient String tempName = "phone";
    public String name = "phone";

    @Expose
    public String exposedName = "phone";
    public int doors = 5;
}
