import {Car, Truck, Vehicle} from "./vehicles"
import {ObjectMapper} from "jackson-js"

const objectMapper = new ObjectMapper();
test("object serialized with JsonTypeInfo.Id.NAME at the java side", () => {
    const jsonData = '[{"@type":"Truck"},{"@type":"Car"},{"@type":"Vehicle"}]'
    const vehicles = objectMapper.parse<Array<Vehicle>>(jsonData, {mainCreator: () => [Array, [Vehicle]]});
    expect(vehicles[0]).toBeInstanceOf(Truck)
    expect(vehicles[1]).toBeInstanceOf(Car)
    expect(vehicles[2]).toBeInstanceOf(Vehicle)
})