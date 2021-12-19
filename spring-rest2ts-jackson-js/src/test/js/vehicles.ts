import {JsonClassType, JsonProperty, JsonSubTypes, JsonTypeInfo, JsonTypeInfoAs, JsonTypeInfoId} from 'jackson-js';

@JsonTypeInfo({
    include: JsonTypeInfoAs.PROPERTY,
    use: JsonTypeInfoId.NAME
})
@JsonSubTypes({
    types: [{
        class: () => Car,
        name: 'Car'
    }, {
        class: () => Truck,
        name: 'Truck'
    }, {
        class: () => Vehicle,
        name: 'Vehicle'
    }]
})
export class Vehicle {
    @JsonProperty()
    @JsonClassType({
        type: () => [String]
    })
    "@type": string;
}

export class Car extends Vehicle {
}

export class Truck extends Vehicle {
}