package com.blueveery.springrest2ts.converters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;

class Product {
    public transient String tempName = "phone";

    public String name = "phone";

    @SerializedName("year")
    public int productionYear = 2021;

    @Expose
    public String exposedName = "phone";

    @Expose(serialize = false, deserialize = false)
    public String falseExposedName = "phone";

    @Expose(deserialize = false)
    public String serializedOnly = "phone";

    @Expose(serialize = false)
    public String deserializedOnly = "phone";

    @Since(2.0)
    public String sinceField = "phone";

    @Until(4.0)
    public String untilField = "phone";

    public int doors = 5;
}
