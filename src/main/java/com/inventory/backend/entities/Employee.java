package com.inventory.backend.entities;

import java.sql.Date;
import java.util.Set;

public class Employee {

    private Long employeeId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date hireDate;
    private String designation;
    private Employee manager;
    private Set<String> emailAddresses;

    // No-args constructor
    public Employee() {
    }

    // All-args constructor
    public Employee(Long employeeId, String firstName, String lastName, String phoneNumber, Date hireDate, String designation, Employee manager, Set<String> emailAddresses) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.hireDate = hireDate;
        this.designation = designation;
        this.manager = manager;
        this.emailAddresses = emailAddresses;
    }

    // Getters
    public Long getEmployeeId() {
        return employeeId;
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

    public Date getHireDate() {
        return hireDate;
    }

    public String getDesignation() {
        return designation;
    }

    public Employee getManager() {
        return manager;
    }

    public Set<String> getEmailAddresses() {
        return emailAddresses;
    }

    // Setters
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setEmailAddresses(Set<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
}
