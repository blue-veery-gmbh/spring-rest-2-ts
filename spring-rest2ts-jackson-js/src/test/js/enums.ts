import {JsonClassType, JsonProperty} from 'jackson-js';

export class Order {
    @JsonProperty()
    @JsonClassType({
        type:() => [Number]
    })
    paymentStatus: OrderPaymentStatus;
    @JsonProperty()
    @JsonClassType({
        type:() => [Object]
    })
    this$0: any;
}

export enum OrderPaymentStatus {
    UNPAID,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED
}