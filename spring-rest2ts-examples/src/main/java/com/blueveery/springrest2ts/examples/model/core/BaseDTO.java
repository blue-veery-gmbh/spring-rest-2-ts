package com.blueveery.springrest2ts.examples.model.core;

import java.util.Date;

public class BaseDTO extends ParametrizedBaseDTO<Integer>{

    private Date updateTimeStamp;

    public Date getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Date updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }
}
