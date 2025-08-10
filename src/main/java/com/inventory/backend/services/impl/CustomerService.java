package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.CustomerDao;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CustomerService implements IModelService<Customer, String> {

    private CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public Optional<Customer> save(Customer customer) {
        customerDao.create(customer);
        return Optional.of(customer);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return customerDao.findById(id);
    }

    @Override
    public List<Customer> findAll() {
        return customerDao.findAll();
    }

    @Override
    public Optional<Customer> update(Customer customer, String id) {
        customerDao.update(customer, id);
        return Optional.of(customer);
    }

    @Override
    public void delete(String id) {
        customerDao.delete(id);
    }

    public Optional<Customer> findByUsername(String username) {
        return customerDao.findByUsername(username);
    }
    
}
