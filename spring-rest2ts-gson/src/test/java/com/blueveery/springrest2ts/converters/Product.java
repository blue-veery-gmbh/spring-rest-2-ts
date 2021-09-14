package com.blueveery.springrest2ts.converters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Product {
    public transient String tempName = "phone";
    public String name = "phone";
    @SerializedName("year")
    public int productionYear = 2021;
    @Expose
    public String exposedName = "phone";
    public int doors = 5;
}
