package com.blueveery.springrest2ts.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class Vehicle_WO {
}

class Truck_WO extends Vehicle_WO {
}

class Car_WO extends Vehicle_WO {
}