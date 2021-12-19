import {JsonClassType, JsonProperty, JsonSubTypes, JsonTypeInfo, JsonTypeInfoAs, JsonTypeInfoId} from 'jackson-js';

@JsonTypeInfo({
    include:JsonTypeInfoAs.PROPERTY,
    property:'@c',
    use:JsonTypeInfoId.NAME
})
@JsonSubTypes({
    types:[{
        class:() => Truck_MCN,
        name:'.Truck_MCN'
    }, {
        class:() => Vehicle_MCN,
        name:'.Vehicle_MCN'
    }, {
        class:() => Car_MCN,
        name:'.Car_MCN'
    }]
})
export class Vehicle_MCN {
    @JsonProperty()
    @JsonClassType({
        type:() => [String]
    })
    "@c": string;
}

export class Car_MCN extends Vehicle_MCN {}

export class Truck_MCN extends Vehicle_MCN {}