import {JsonSubTypes, JsonTypeInfo, JsonTypeInfoAs, JsonTypeInfoId} from 'jackson-js';

@JsonTypeInfo({
    include:JsonTypeInfoAs.WRAPPER_OBJECT,
    use:JsonTypeInfoId.NAME
})
@JsonSubTypes({
    types:[{
        class:() => Truck_WO,
        name:'.Truck_WO'
    }, {
        class:() => Vehicle_WO,
        name:'.Vehicle_WO'
    }, {
        class:() => Car_WO,
        name:'.Car_WO'
    }]
})
export class Vehicle_WO {}

export class Car_WO extends Vehicle_WO {}

export class Truck_WO extends Vehicle_WO {}