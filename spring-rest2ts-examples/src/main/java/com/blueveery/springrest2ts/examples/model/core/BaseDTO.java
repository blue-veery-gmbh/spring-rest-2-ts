package com.blueveery.springrest2ts.examples.model.core;

import java.util.Date;

public class BaseDTO {
    private int id;
    private Date updateTimeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Date updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }
}
