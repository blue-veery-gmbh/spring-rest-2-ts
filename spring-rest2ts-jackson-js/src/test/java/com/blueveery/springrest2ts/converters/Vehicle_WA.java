package com.blueveery.springrest2ts.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
public class Vehicle_WA {
}

class Truck_WA extends Vehicle_WA {
}

class Car_WA extends Vehicle_WA {
}