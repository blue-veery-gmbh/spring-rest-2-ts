package com.blueveery.springrest2ts.examples.model.core;

import java.util.Date;

public class BaseDTO {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    Date updateTimeStamp;
}
