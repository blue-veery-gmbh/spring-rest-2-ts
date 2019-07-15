package com.blueveery.springrest2ts.examples.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderDeliveryStatus {
    ORDERED("ORDERED"),
    IN_DELIVERY("IN_DELIVERY"),
    DELIVERED("DELIVERED");

    private String statusName;

    OrderDeliveryStatus(String status) {
        this.statusName = status;
    }

    public String getStatusName() {
        return statusName;
    }

    @JsonValue
    public String getDeliveryStatusDescription() {
        return "Delivery status: " + getStatusName();
    }
}