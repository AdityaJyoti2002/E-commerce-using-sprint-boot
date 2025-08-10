package com.inventory.backend.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class CustomerOrder {

    private Long orderId;
    private Customer customer;
    private Date dateOfOrder;
    private PaymentMethod paymentMethod;
    private Employee processorEmployee;
    private Set<Pair<Product, Integer>> products = new HashSet<>();

    // --- No-args constructor ---
    public CustomerOrder() {
    }

    // --- All-args constructor ---
    public CustomerOrder(Long orderId, Customer customer, Date dateOfOrder, PaymentMethod paymentMethod, Employee processorEmployee, Set<Pair<Product, Integer>> products) {
        this.orderId = orderId;
        this.customer = customer;
        this.dateOfOrder = dateOfOrder;
        this.paymentMethod = paymentMethod;
        this.processorEmployee = processorEmployee;
        this.products = products != null ? products : new HashSet<>();
    }

    // --- Getters ---
    public Long getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Employee getProcessorEmployee() {
        return processorEmployee;
    }

    public Set<Pair<Product, Integer>> getProducts() {
        return products;
    }

    // --- Setters ---
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setProcessorEmployee(Employee processorEmployee) {
        this.processorEmployee = processorEmployee;
    }

    public void setProducts(Set<Pair<Product, Integer>> products) {
        this.products = products;
    }

    // --- Method to get quantity by productId ---
    public Integer getQuantity(Long productId) {
        for (Pair<Product, Integer> pair : products) {
            if (pair.getFirst().getProductId().equals(productId)) {
                return pair.getSecond();
            }
        }
        return 0;
    }

    // --- Inner static Pair class ---
    public static class Pair<T, U> {
        private final T first;
        private final U second;
    
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    
        public T getFirst() {
            return first;
        }
    
        public U getSecond() {
            return second;
        }
    }
    

    // --- Enum for PaymentMethod ---
    public enum PaymentMethod {
        CASH,
        CARD,
        NET_BANKING,
        UPI
    }
}
