package com.inventory.backend.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

public class Cart {

    private Long cartId;
    private Customer customer;
    private Set<Triple<Product, Integer, Date>> products = new HashSet<>();

    // No-args constructor
    public Cart() {
    }

    // All-args constructor
    public Cart(Long cartId, Customer customer, Set<Triple<Product, Integer, Date>> products) {
        this.cartId = cartId;
        this.customer = customer;
        this.products = products;
    }

    // Getters
    public Long getCartId() {
        return cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Set<Triple<Product, Integer, Date>> getProducts() {
        return products;
    }

    // Setters
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProducts(Set<Triple<Product, Integer, Date>> products) {
        this.products = products;
    }

    // toString
    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", customer=" + customer +
                ", products=" + products +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;

        Cart cart = (Cart) o;

        if (cartId != null ? !cartId.equals(cart.cartId) : cart.cartId != null) return false;
        if (customer != null ? !customer.equals(cart.customer) : cart.customer != null) return false;
        return products != null ? products.equals(cart.products) : cart.products == null;
    }

    // hashCode
    @Override
    public int hashCode() {
        int result = cartId != null ? cartId.hashCode() : 0;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        return result;
    }
}
