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

    const keyboard = objectMapper.parse<Keyboard>('{"keyNumber": 1}', {mainCreator: () => [Keyboard]});
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

test("object field should be deserialized with proper type", () => {

    class Product {
        @JsonProperty()
        @JsonClassType({
            type:() => [Keyboard]
        })
        keyboard: Keyboard;
    }

    class Keyboard {
        @JsonProperty()
        @JsonClassType({
            type:() => [Number]
        })
        keyNumber: number;
    }

    const product = objectMapper.parse<Product>('{"keyboard": {"keyNumber": 1}}', {mainCreator: () => [Product]});
    expect(product).toBeInstanceOf(Product)
    expect(product.keyboard).toBeInstanceOf(Keyboard)
})

test("array of strings field should be deserialized with proper type", () => {

    class User {
        @JsonProperty()
        @JsonClassType({
            type:() => [Array, [String]]
        })
        roleList: string[];
    }

    const user = objectMapper.parse<User>('{"roleList": ["admin", "hr"]}', {mainCreator: () => [User]});
    expect(user).toBeInstanceOf(User)
    expect(user.roleList).toBeInstanceOf(Array)
})

test("date field should be deserialized with proper type", () => {

    class User {
        @JsonProperty()
        @JsonClassType({
            type: () => [Date]
        })
        joinDate: Date;
    }

    const user = objectMapper.parse<User>('{"joinDate": 1639434853477}', {mainCreator: () => [User]});
    expect(user).toBeInstanceOf(User)
    expect(user.joinDate).toBeInstanceOf(Date)
})
