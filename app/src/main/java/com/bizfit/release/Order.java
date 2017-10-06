package com.bizfit.release;

/**
 * Created by iipa on 25.9.2017.
 */

public class Order {

    String customerID;
    String expertID;
    String currency;
    Float price;
    String timeStamp;
    OrderStatus status;

    public Order() {

    }

    public Order(String customer, String expert, String currency, Float price, String timeStamp, OrderStatus status) {
        this.customerID = customer;
        this.expertID = expert;
        this.currency = currency;
        this.price = price;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    enum OrderStatus {
        ACTIVE, CANCELING, COMPLETED
    }

}
