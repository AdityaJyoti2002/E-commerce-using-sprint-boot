package com.inventory.backend.entities;

import java.util.Set;

public class Manufacturer {
    
    private Long manufacturerId;
    private String manufacturerName;
    private String address;
    private Set<String> contactNumbers;
    private Set<String> emailIds;

    // No-args constructor
    public Manufacturer() {}

    // All-args constructor
    public Manufacturer(Long manufacturerId, String manufacturerName, String address, Set<String> contactNumbers, Set<String> emailIds) {
        this.manufacturerId = manufacturerId;
        this.manufacturerName = manufacturerName;
        this.address = address;
        this.contactNumbers = contactNumbers;
        this.emailIds = emailIds;
    }

    // Getters
    public Long getManufacturerId() {
        return manufacturerId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getAddress() {
        return address;
    }

    public Set<String> getContactNumbers() {
        return contactNumbers;
    }

    public Set<String> getEmailIds() {
        return emailIds;
    }

    // Setters
    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactNumbers(Set<String> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public void setEmailIds(Set<String> emailIds) {
        this.emailIds = emailIds;
    }

    // Additional methods
    public void addContactNumber(String contactNumber) {
        this.contactNumbers.add(contactNumber);
    }

    public void addEmailId(String emailAddress) {
        this.emailIds.add(emailAddress);
    }
}
