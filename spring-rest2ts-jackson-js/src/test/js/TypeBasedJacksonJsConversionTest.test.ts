import {JsonClassType, JsonProperty, ObjectMapper} from "jackson-js";


const objectMapper = new ObjectMapper();

test("field with json property should be serialized", () => {

    class Keyboard {
        @JsonProperty()
        keyNumber: number
    }

    const keyboard = new Keyboard()
    keyboard.keyNumber = 1
    expect(JSON.parse(objectMapper.stringify<Keyboard>(keyboard))).toEqual({keyNumber: 1})
})

test("field with json property should be deserialized", () => {

    class Keyboard {
        @JsonProperty()
        keyNumber: number;
    }

    let keyboard = objectMapper.parse<Keyboard>('{"keyNumber": 1}', {mainCreator: () => [Keyboard]})
    expect(keyboard).toEqual({keyNumber: 1})
    expect(keyboard).toBeInstanceOf(Keyboard)
})

test("field serialization with number JsonClassType should succeed", () => {

    class Keyboard {
        @JsonProperty()
        @JsonClassType({
            type:() => [Number]
        })
        keyNumber: number;
    }

    expect(objectMapper.parse<Keyboard>('{"keyNumber": 1}')).toEqual({keyNumber: 1})
})
