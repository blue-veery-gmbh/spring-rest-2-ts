import {
    JsonClassType,
    JsonProperty,
    JsonSubTypes,
    JsonTypeInfo,
    JsonTypeInfoAs,
    JsonTypeInfoId,
    JsonTypeName
} from 'jackson-js';

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
@JsonTypeName({
    value: 'Vehicle'
})
export class Vehicle {
    @JsonProperty()
    @JsonClassType({
        type: () => [String]
    })
    "@type": string;
}

@JsonTypeName({
    value: 'Car'
})
export class Car extends Vehicle {
}

@JsonTypeName({
    value: 'Truck'
})
export class Truck extends Vehicle {
}