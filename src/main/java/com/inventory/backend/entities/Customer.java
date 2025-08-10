package com.inventory.backend.entities;

public class Customer {

    private String emailId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String shippingAddress;
    private String billingAddress;
    private String username;

    // No-args constructor
    public Customer() {
    }

    // All-args constructor
    public Customer(String emailId, String firstName, String lastName, String phoneNumber,
                    String shippingAddress, String billingAddress, String username) {
        this.emailId = emailId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.username = username;
    }

    // Getters
    public String getEmailId() {
        return emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
