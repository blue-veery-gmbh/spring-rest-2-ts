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

    test("set of string should be deserialized with proper type", () => {

        class User {
            @Type(() => String)
            tagsSet: Set<string>;
        }

        const user = plainToInstance(User,{"tagsSet": ["admin", "hr"]});
        expect(user).toBeInstanceOf(User)
        expect(user.tagsSet).toBeInstanceOf(Set)
        expect(user.tagsSet.size).toEqual(2)
    })

    test("date field should be deserialized with proper type", () => {

        class User {
            @Type(() => Date)
            joinDate: Date;
        }

        const user =  plainToInstance(User,{"joinDate": 1639434853477});
        expect(user).toBeInstanceOf(User)
        expect(user.joinDate).toBeInstanceOf(Date)
    })


    test("map of strings field should be deserialized with proper type", () => {
        class User {
            @Type(() => String)
            tagsMap: Map<string, string>;
        }

        const user =  plainToInstance(User,{tagsMap: {green: "safe", red: "danger"}});
        expect(user).toBeInstanceOf(User)
        expect(user.tagsMap).toBeInstanceOf(Map)
        expect(user.tagsMap.size).toEqual(2)
    })

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
    //     const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', );
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
    //     const user = objectMapper.parse<User>('{"numbersMap": {"one": 1, "two": 2}}', );
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
    //     const user = objectMapper.parse<User>('{"datesMap": {"today": 1639498278935}}', );
    //     expect(user).toBeInstanceOf(User)
    //     expect(user.datesMap).toBeInstanceOf(Map)
    //     expect(user.datesMap.get('today')).toBeInstanceOf(Date)
    //     expect(user.datesMap.size).toEqual(1)
    // })
})