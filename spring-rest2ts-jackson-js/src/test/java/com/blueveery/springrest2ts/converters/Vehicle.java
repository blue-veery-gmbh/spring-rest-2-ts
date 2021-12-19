package com.blueveery.springrest2ts.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public class Vehicle {
}

class Truck extends Vehicle {
}

class Car extends Vehicle {
}