package com.inventory.backend.entities;

import java.sql.Date;

public class OrderReturns {

    private CustomerOrder order;
    private Date returnDate;
    private String returnReason;

    // No-args constructor
    public OrderReturns() {
    }

    // All-args constructor
    public OrderReturns(CustomerOrder order, Date returnDate, String returnReason) {
        this.order = order;
        this.returnDate = returnDate;
        this.returnReason = returnReason;
    }

    // Getters
    public CustomerOrder getOrder() {
        return order;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public String getReturnReason() {
        return returnReason;
    }

    // Setters
    public void setOrder(CustomerOrder order) {
        this.order = order;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }
}
