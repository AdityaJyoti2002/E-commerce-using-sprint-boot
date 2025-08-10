package com.inventory.backend.entities;

import java.sql.Date;

public class ShippingInfoManufacturerOrder {

    private Long shippingInfoId;
    private Date shippingDate;
    private Date expectedDeliveryDate;
    private Status status;
    private ManufacturerOrder manufacturerOrder;

    public enum Status {
        SHIPPED,
        ARRIVED,
        PENDING,
        CANCELLED
    }

    // No-args constructor
    public ShippingInfoManufacturerOrder() {
    }

    // All-args constructor
    public ShippingInfoManufacturerOrder(Long shippingInfoId, Date shippingDate, Date expectedDeliveryDate, Status status, ManufacturerOrder manufacturerOrder) {
        this.shippingInfoId = shippingInfoId;
        this.shippingDate = shippingDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
        this.manufacturerOrder = manufacturerOrder;
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

    public ManufacturerOrder getManufacturerOrder() {
        return manufacturerOrder;
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

    public void setManufacturerOrder(ManufacturerOrder manufacturerOrder) {
        this.manufacturerOrder = manufacturerOrder;
    }
}
