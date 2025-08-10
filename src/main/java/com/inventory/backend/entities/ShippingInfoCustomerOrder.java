package com.inventory.backend.entities;

import java.sql.Date;

public class ShippingInfoCustomerOrder {

    private Long shippingInfoId;
    private Date shippingDate;
    private Date expectedDeliveryDate;
    private Status status;
    private CustomerOrder order;

    // No-args constructor
    public ShippingInfoCustomerOrder() {
    }

    // All-args constructor
    public ShippingInfoCustomerOrder(Long shippingInfoId, Date shippingDate, Date expectedDeliveryDate, Status status, CustomerOrder order) {
        this.shippingInfoId = shippingInfoId;
        this.shippingDate = shippingDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
        this.order = order;
    }

    // Getters
    public Long getShippingInfoId() {
        return shippingInfoId;
    }

    public Date getShippingDate() {
        return shippingDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public Status getStatus() {
        return status;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    // Setters
    public void setShippingInfoId(Long shippingInfoId) {
        this.shippingInfoId = shippingInfoId;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }

    // Enum definition
    public enum Status {
        SHIPPED,
        DELIVERED,
        PENDING,
        CANCELLED
    }
}
