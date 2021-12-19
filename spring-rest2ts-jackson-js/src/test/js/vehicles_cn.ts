import {JsonClassType, JsonProperty, JsonSubTypes, JsonTypeInfo, JsonTypeInfoAs, JsonTypeInfoId} from 'jackson-js';

@JsonTypeInfo({
    include: JsonTypeInfoAs.PROPERTY,
    property: '@class',
    use: JsonTypeInfoId.NAME
})
@JsonSubTypes({
    types: [{
        class: () => Truck_CN,
        name: 'com.blueveery.springrest2ts.converters.Truck_CN'
    }, {
        class: () => Vehicle_CN,
        name: 'com.blueveery.springrest2ts.converters.Vehicle_CN'
    }, {
        class: () => Car_CN,
        name: 'com.blueveery.springrest2ts.converters.Car_CN'
    }]
})
export class Vehicle_CN {
    @JsonProperty()
    @JsonClassType({
        type: () => [String]
    })
    "@class": string;
}

export class Car_CN extends Vehicle_CN {
}

export class Truck_CN extends Vehicle_CN {
}