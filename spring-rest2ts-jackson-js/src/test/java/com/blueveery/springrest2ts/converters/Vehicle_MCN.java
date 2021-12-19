package com.blueveery.springrest2ts.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
public class Vehicle_MCN {
}

class Truck_MCN extends Vehicle_MCN {
}

class Car_MCN extends Vehicle_MCN {
}