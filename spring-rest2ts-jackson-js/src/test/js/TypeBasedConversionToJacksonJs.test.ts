import {JsonClassType, JsonProperty, ObjectMapper} from "jackson-js";
import {Order, OrderPaymentStatus} from "./enums";

describe("checks for jackson-js annotation generated based on java type info", () => {
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
                type: () => [Number]
            })
            keyNumber: number;
        }

        expect(objectMapper.parse<Keyboard>('{"keyNumber": 1}')).toEqual({keyNumber: 1})
    })

    test("object field should be deserialized with proper type", () => {

        class Product {
            @JsonProperty()
            @JsonClassType({
                type: () => [Keyboard]
            })
            keyboard: Keyboard;
        }

        class Keyboard {
            @JsonProperty()
            @JsonClassType({
                type: () => [Number]
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
                type: () => [Array, [String]]
            })
            roleList: string[];
        }

        const user = objectMapper.parse<User>('{"roleList": ["admin", "hr"]}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.roleList).toBeInstanceOf(Array)
    })

    test("set of string should be deserialized with proper type", () => {

        class User {
            @JsonProperty()
            @JsonClassType({
                type: () => [Set, [String]]
            })
            tagsHashSet: Set<string>;
        }

        const user = objectMapper.parse<User>('{"tagsHashSet": ["admin", "hr"]}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.tagsHashSet).toBeInstanceOf(Set)
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


    test("map of strings field should be deserialized with proper type", () => {

        class User {
            @JsonProperty()
            @JsonClassType({
                type: () => [Map, [String, String]]
            })
            tagsMap: Map<string, string>;
        }

        const user = objectMapper.parse<User>('{"tagsMap": {"green": "safe", "red": "danger"}}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.tagsMap).toBeInstanceOf(Map)
        expect(user.tagsMap.size).toEqual(2)
    })

    test("map of numbers field should be deserialized with proper type", () => {

        class User {
            @JsonProperty()
            @JsonClassType({
                type: () => [Map, [String, Number]]
            })
            numbersMap: Map<string, number>;
        }

        const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.numbersMap).toBeInstanceOf(Map)
        expect(user.numbersMap.get('one')).toBe(1)
        expect(user.numbersMap.size).toEqual(2)
    })

    test("map of numbers mapped to JS object field should be deserialized with proper type", () => {

        class User {
            @JsonProperty()
            @JsonClassType({
                type: () => [Object]
            })
            numbersMap: { [key: string]: number };
        }

        const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.numbersMap).toBeInstanceOf(Object)
        expect(user.numbersMap['one']).toBe(1)
    })


    test("map of dates field should be deserialized with proper type", () => {

        class User {
            @JsonProperty()
            @JsonClassType({
                type: () => [Map, [String, Date]]
            })
            datesMap: Map<string, Date>;
        }

        const user = objectMapper.parse<User>('{"datesMap": {"today": 1639498278935}}', {mainCreator: () => [User]});
        expect(user).toBeInstanceOf(User)
        expect(user.datesMap).toBeInstanceOf(Map)
        expect(user.datesMap.get('today')).toBeInstanceOf(Date)
        expect(user.datesMap.size).toEqual(1)
    })

    test("enums should be serialized and deserialized with proper type", () => {
        const order = new Order();
        order.paymentStatus = OrderPaymentStatus.PAYMENT_FAILED
        let jsonData = objectMapper.stringify(order);

        const clonedOrder = objectMapper.parse<Order>(jsonData, {mainCreator: () => [Order]});
        expect(clonedOrder).toBeInstanceOf(Order)
        expect(clonedOrder.paymentStatus).toBe(OrderPaymentStatus.PAYMENT_FAILED)
    })
})