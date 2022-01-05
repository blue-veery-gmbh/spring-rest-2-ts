package com.blueveery.springrest2ts.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Vehicle_CN {
}

class Truck_CN extends Vehicle_CN {
}

class Car_CN extends Vehicle_CN {
}