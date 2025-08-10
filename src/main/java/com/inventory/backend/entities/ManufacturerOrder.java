package com.inventory.backend.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class ManufacturerOrder {

    private Long orderId;
    private Manufacturer manufacturer;
    private Employee processedByEmployee;
    private Date dateOfOrder;
    private Set<CustomerOrder.Pair<Product, Integer>> products = new HashSet<>();

    // No-args constructor
    public ManufacturerOrder() {
    }

    // All-args constructor
    public ManufacturerOrder(Long orderId, Manufacturer manufacturer, Employee processedByEmployee,
                             Date dateOfOrder, Set<CustomerOrder.Pair<Product, Integer>> products) {
        this.orderId = orderId;
        this.manufacturer = manufacturer;
        this.processedByEmployee = processedByEmployee;
        this.dateOfOrder = dateOfOrder;
        this.products = products;
    }

    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public Employee getProcessedByEmployee() {
        return processedByEmployee;
    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public Set<CustomerOrder.Pair<Product, Integer>> getProducts() {
        return products;
    }

    // Setters
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setProcessedByEmployee(Employee processedByEmployee) {
        this.processedByEmployee = processedByEmployee;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public void setProducts(Set<CustomerOrder.Pair<Product, Integer>> products) {
        this.products = products;
    }
}
