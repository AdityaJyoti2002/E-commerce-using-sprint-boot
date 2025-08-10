package com.inventory.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.*;
import com.inventory.backend.services.impl.ShippingInfoManufacturerOrderService;

@Repository
public class ManufacturerOrderDao implements IDao<ManufacturerOrder, Long> {

    private final JdbcTemplate jdbcTemplate;
    private final ShippingInfoManufacturerOrderService shippingInfoManufacturerOrderService;

    public ManufacturerOrderDao(JdbcTemplate jdbcTemplate,
                                ShippingInfoManufacturerOrderService shippingInfoManufacturerOrderService) {
        this.jdbcTemplate = jdbcTemplate;
        this.shippingInfoManufacturerOrderService = shippingInfoManufacturerOrderService;
    }

    @Override
    public void create(ManufacturerOrder a) {
        String sql = "INSERT INTO manufacturer_orders (ordered_from, processed_by_employee_id, date_of_order) VALUES (?, ?, ?)";

        try (PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getManufacturer().getManufacturerId());
            ps.setLong(2, a.getProcessedByEmployee().getEmployeeId());
            ps.setDate(3, a.getDateOfOrder());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    long insertId = keys.getLong(1);
                    String sql2 = "INSERT INTO manufacturer_orders_products (manufacturer_order_id, product_id, quantity) VALUES (?, ?, ?)";
                    jdbcTemplate.batchUpdate(sql2,
                            a.getProducts().stream()
                             .map(pair -> new Object[]{insertId, pair.getFirst().getProductId(), pair.getSecond()})
                             .toList());

                    a.setOrderId(insertId);
                    ShippingInfoManufacturerOrder shippingInfo = new ShippingInfoManufacturerOrder();
                    shippingInfo.setShippingDate(null);
                    shippingInfo.setExpectedDeliveryDate(null);
                    shippingInfo.setStatus(ShippingInfoManufacturerOrder.Status.PENDING);
                    shippingInfo.setManufacturerOrder(a);
                    shippingInfoManufacturerOrderService.save(shippingInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<ManufacturerOrder> findById(Long id) {
        String sql = "SELECT mo.*, m.*, p.*, e.*, mop.quantity, c.category_id FROM manufacturer_orders mo " +
                "LEFT JOIN manufacturers m ON mo.ordered_from = m.manufacturer_id " +
                "LEFT JOIN manufacturer_orders_products mop ON mo.order_id = mop.manufacturer_order_id " +
                "LEFT JOIN products p ON mop.product_id = p.product_id " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN employees e ON mo.processed_by_employee_id = e.employee_id WHERE mo.order_id = ?";
        TreeMap<Long, ManufacturerOrder> manufacturerOrderMap = jdbcTemplate.query(sql, new ManufacturerOrderRowMapper(), id);
        return manufacturerOrderMap.values().stream().findFirst();
    }

    @Override
    public List<ManufacturerOrder> findAll() {
        String sql = "SELECT mo.*, m.*, p.*, e.*, mop.quantity, c.category_id FROM manufacturer_orders mo " +
                "LEFT JOIN manufacturers m ON mo.ordered_from = m.manufacturer_id " +
                "LEFT JOIN manufacturer_orders_products mop ON mo.order_id = mop.manufacturer_order_id " +
                "LEFT JOIN products p ON mop.product_id = p.product_id " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN employees e ON mo.processed_by_employee_id = e.employee_id";
        TreeMap<Long, ManufacturerOrder> manufacturerOrderMap = jdbcTemplate.query(sql, new ManufacturerOrderRowMapper());
        return new ArrayList<>(manufacturerOrderMap.values());
    }

    @Override
    public void update(ManufacturerOrder a, Long id) {
        String sql = "UPDATE manufacturer_orders SET ordered_from = ?, processed_by_employee_id = ?, date_of_order = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, a.getManufacturer().getManufacturerId(), a.getProcessedByEmployee().getEmployeeId(), a.getDateOfOrder(), id);

        jdbcTemplate.update("DELETE FROM manufacturer_orders_products WHERE manufacturer_order_id = ?", id);

        String insertProductsSql = "INSERT INTO manufacturer_orders_products (manufacturer_order_id, product_id, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(insertProductsSql,
                a.getProducts().stream()
                 .map(pair -> new Object[]{id, pair.getFirst().getProductId(), pair.getSecond()})
                 .toList());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM manufacturer_orders WHERE order_id = ?", id);
    }

    public static class ManufacturerOrderRowMapper implements ResultSetExtractor<TreeMap<Long, ManufacturerOrder>> {
        @Override
        public TreeMap<Long, ManufacturerOrder> extractData(ResultSet rs) throws SQLException, DataAccessException {
            TreeMap<Long, ManufacturerOrder> manufacturerOrderMap = new TreeMap<>();

            while (rs.next()) {
                Long orderId = rs.getLong("order_id");

                ManufacturerOrder order = manufacturerOrderMap.get(orderId);
                if (order == null) {
                    // Manufacturer
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setManufacturerId(rs.getLong("manufacturer_id"));
                    manufacturer.setManufacturerName(rs.getString("manufacturer_name"));
                    manufacturer.setAddress(rs.getString("address"));

                    // Employee
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getLong("employee_id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setPhoneNumber(rs.getString("phone_number"));
                    employee.setHireDate(rs.getDate("hire_date"));
                    employee.setDesignation(rs.getString("designation"));

                    // ManufacturerOrder
                    order = new ManufacturerOrder();
                    order.setOrderId(orderId);
                    order.setManufacturer(manufacturer);
                    order.setProcessedByEmployee(employee);
                    order.setDateOfOrder(rs.getDate("date_of_order"));
                    order.setProducts(new HashSet<>());

                    manufacturerOrderMap.put(orderId, order);
                }

                // Category
                Category category = new Category();
                category.setCategoryId(rs.getLong("category_id"));

                // Product
                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setExpiryDate(rs.getDate("expiry_date"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setSellingPrice(rs.getDouble("selling_price"));
                product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));
                product.setCategory(category);

                int quantity = rs.getInt("quantity");
                order.getProducts().add(new CustomerOrder.Pair<>(product, quantity));
            }

            return manufacturerOrderMap;
        }
    }
}