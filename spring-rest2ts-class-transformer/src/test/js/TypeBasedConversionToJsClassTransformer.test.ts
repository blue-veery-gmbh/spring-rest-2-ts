import {instanceToPlain, plainToInstance, Type} from "class-transformer";
import 'reflect-metadata';

describe("checks for class-transformer annotation generated based on java type info", () => {

    test("field with json property should be serialized", () => {

        class Keyboard {
            keyNumber: number
        }

        const keyboard = new Keyboard()
        keyboard.keyNumber = 1
        expect(instanceToPlain(keyboard)).toEqual({keyNumber: 1})
    })

    test("object field should be deserialized with proper type", () => {

        class Product {
            @Type(() => Keyboard)
            keyboard: Keyboard;
        }

        class Keyboard {
            keyNumber: number;
        }

        const product = plainToInstance(Product, {"keyboard": {"keyNumber": 1}});
        expect(product).toBeInstanceOf(Product)
        expect(product.keyboard).toBeInstanceOf(Keyboard)
    })

    test("array of strings field should be deserialized with proper type", () => {

        class User {
            @Type(() => String)
            roleList: string[];
        }

        const user = plainToInstance(User, {"roleList": ["admin", "hr"]})
        expect(user).toBeInstanceOf(User)
        expect(user.roleList).toBeInstanceOf(Array)
    })
    //
    // test("set of string should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Set, [String]]
    //         })
    //         tagsHashSet: Set<string>;
    //     }
    //
    //     const user = objectMapper.parse<User>('{"tagsHashSet": ["admin", "hr"]}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.tagsHashSet).toBeInstanceOf(Set)
    // })
    //
    // test("date field should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Date]
    //         })
    //         joinDate: Date;
    //     }
    //
    //     const user = objectMapper.parse<User>('{"joinDate": 1639434853477}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.joinDate).toBeInstanceOf(Date)
    // })
    //
    //
    // test("map of strings field should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Map, [String, String]]
    //         })
    //         tagsMap: Map<string, string>;
    //     }
    //
    //     const user = objectMapper.parse<User>('{"tagsMap": {"green": "safe", "red": "danger"}}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.tagsMap).toBeInstanceOf(Map)
    //     expect(user.tagsMap.size).toEqual(2)
    // })
    //
    // test("map of numbers field should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Map, [String, Number]]
    //         })
    //         numbersMap: Map<string, number>;
    //     }
    //
    //     const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.numbersMap).toBeInstanceOf(Map)
    //     expect(user.numbersMap.get('one')).toBe(1)
    //     expect(user.numbersMap.size).toEqual(2)
    // })
    //
    // test("map of numbers mapped to JS object field should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Object]
    //         })
    //         numbersMap: { [key: string]: number };
    //     }
    //
    //     const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.numbersMap).toBeInstanceOf(Object)
    //     expect(user.numbersMap['one']).toBe(1)
    // })
    //
    //
    // test("map of dates field should be deserialized with proper type", () => {
    //
    //     class User {
    //         @JsonProperty()
    //         @JsonClassType({
    //             type: () => [Map, [String, Date]]
    //         })
    //         datesMap: Map<string, Date>;
    //     }
    //
    //     const user = objectMapper.parse<User>('{"datesMap": {"today": 1639498278935}}', {mainCreator: () => [User]});
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.datesMap).toBeInstanceOf(Map)
    //     expect(user.datesMap.get('today')).toBeInstanceOf(Date)
    //     expect(user.datesMap.size).toEqual(1)
    // })
})