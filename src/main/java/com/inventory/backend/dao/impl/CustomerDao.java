package com.inventory.backend.dao.impl;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Customer;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDao implements IDao<Customer, String> {


    private final JdbcTemplate jdbcTemplate;

    public CustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, phone_number, shipping_address, billing_address, email, username) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), customer.getShippingAddress(), customer.getBillingAddress(), customer.getEmailId(), customer.getUsername());
    }

    @Override
    public Optional<Customer> findById(String id) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        List<Customer> results = jdbcTemplate.query(sql, new CustomerRowMapper(), id);
        return results.stream().findFirst();
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, new CustomerRowMapper());
    }

    @Override
    public void update(Customer customer, String id) {
        String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ?, shipping_address = ?, billing_address = ? WHERE email = ?";
        jdbcTemplate.update(sql, customer.getEmailId(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), customer.getShippingAddress(), customer.getBillingAddress(), id);
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM customers WHERE email = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Customer> findByUsername(String username) {
        String sql = "SELECT * FROM customers WHERE username = ?";
        List<Customer> results = jdbcTemplate.query(sql, new CustomerRowMapper(), username);
        return results.stream().findFirst();
    }

    public static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Customer customer = new Customer();
                    customer.setEmailId(rs.getString("email"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customer.setShippingAddress(rs.getString("shipping_address"));
                    customer.setBillingAddress(rs.getString("billing_address"));
                    customer.setUsername(rs.getString("username"));
                    return customer;
        }
    }
    
}
