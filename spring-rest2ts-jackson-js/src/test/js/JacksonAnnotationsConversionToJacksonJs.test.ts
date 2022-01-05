import {Car, Truck, Vehicle} from "./vehicles"
import {ObjectMapper} from "jackson-js"
import {Car_CN, Truck_CN, Vehicle_CN} from "./vehicles_cn";
import {Car_MCN, Truck_MCN, Vehicle_MCN} from "./vehicles_mcn";
import {Car_WO, Truck_WO, Vehicle_WO} from "./vehicles_wo";
import {Car_WA, Truck_WA, Vehicle_WA} from "./vehicles_wa";

describe('checks content serialized at java side is deserialized into generated TS classes', () => {
    const objectMapper = new ObjectMapper();

    test("object serialized with JsonTypeInfo.Id.NAME at the java side", () => {
        const jsonData = '[{"@type":"Truck"},{"@type":"Car"},{"@type":"Vehicle"}]'
        const vehicles = objectMapper.parse<Vehicle[]>(jsonData, {mainCreator: () => [Array, [Vehicle]]});
        expect(vehicles[0]).toBeInstanceOf(Truck)
        expect(vehicles[1]).toBeInstanceOf(Car)
        expect(vehicles[2]).toBeInstanceOf(Vehicle)
    })

    test("object serialized with JsonTypeInfo.Id.CLASS at the java side", () => {
        const jsonData = '[{"@class":"com.blueveery.springrest2ts.converters.Truck_CN"},{"@class":"com.blueveery.springrest2ts.converters.Car_CN"},{"@class":"com.blueveery.springrest2ts.converters.Vehicle_CN"}]'
        const vehicles = objectMapper.parse<Array<Vehicle_CN>>(jsonData, {mainCreator: () => [Array, [Vehicle_CN]]});
        expect(vehicles[0]).toBeInstanceOf(Truck_CN)
        expect(vehicles[1]).toBeInstanceOf(Car_CN)
        expect(vehicles[2]).toBeInstanceOf(Vehicle_CN)
    })

    test("object serialized with JsonTypeInfo.Id.MINIMAL_CLASS at the java side", () => {
        const jsonData = '[{"@c":".Truck_MCN"},{"@c":".Car_MCN"},{"@c":".Vehicle_MCN"}]'
        const vehicles = objectMapper.parse<Array<Vehicle_MCN>>(jsonData, {mainCreator: () => [Array, [Vehicle_MCN]]});
        expect(vehicles[0]).toBeInstanceOf(Truck_MCN)
        expect(vehicles[1]).toBeInstanceOf(Car_MCN)
        expect(vehicles[2]).toBeInstanceOf(Vehicle_MCN)
    })

    test("object serialized with JsonTypeInfo.Id.MINIMAL_CLASS and WRAPPER_OBJECT at the java side", () => {
        const jsonData = '[{".Truck_WO":{}},{".Car_WO":{}},{".Vehicle_WO":{}}]'
        const vehicles = objectMapper.parse<Array<Vehicle_WO>>(jsonData, {mainCreator: () => [Array, [Vehicle_WO]]});
        expect(vehicles[0]).toBeInstanceOf(Truck_WO)
        expect(vehicles[1]).toBeInstanceOf(Car_WO)
        expect(vehicles[2]).toBeInstanceOf(Vehicle_WO)
    })

    test("object serialized with JsonTypeInfo.Id.MINIMAL_CLASS and WRAPPER_ARRAY at the java side", () => {
        const jsonData = '[[".Truck_WA",{}],[".Car_WA",{}],[".Vehicle_WA",{}]]'
        const vehicles = objectMapper.parse<Array<Vehicle_WA>>(jsonData, {mainCreator: () => [Array, [Vehicle_WA]]});
        expect(vehicles[0]).toBeInstanceOf(Truck_WA)
        expect(vehicles[1]).toBeInstanceOf(Car_WA)
        expect(vehicles[2]).toBeInstanceOf(Vehicle_WA)
    })
})