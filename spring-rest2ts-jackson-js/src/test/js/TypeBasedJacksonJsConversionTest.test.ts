import {JsonProperty, ObjectMapper} from "jackson-js";

const objectMapper = new ObjectMapper();

test("Class field with json property should be serialized", () => {

    class Keyboard {
        @JsonProperty()
        keyNumber: number;
    }

    const keyboard = new Keyboard();
    keyboard.keyNumber = 1
    expect(JSON.parse(objectMapper.stringify<Keyboard>(keyboard))).toEqual({keyNumber: 1})
})

test("Class field with json property should be deserialized", () => {

    class Keyboard {
        @JsonProperty()
        keyNumber: number;
    }

    expect(objectMapper.parse<Keyboard>('{"keyNumber": 1}')).toEqual({keyNumber: 1})
})