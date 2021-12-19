import {JsonSubTypes, JsonTypeInfo, JsonTypeInfoAs, JsonTypeInfoId} from 'jackson-js';

@JsonTypeInfo({
    include: JsonTypeInfoAs.WRAPPER_ARRAY,
    use: JsonTypeInfoId.NAME
})
@JsonSubTypes({
    types: [{
        class: () => Truck_WA,
        name: '.Truck_WA'
    }, {
        class: () => Vehicle_WA,
        name: '.Vehicle_WA'
    }, {
        class: () => Car_WA,
        name: '.Car_WA'
    }]
})
export class Vehicle_WA {
}

export class Car_WA extends Vehicle_WA {
}

export class Truck_WA extends Vehicle_WA {
}