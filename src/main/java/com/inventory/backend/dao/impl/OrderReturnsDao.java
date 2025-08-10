package com.inventory.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.Employee;
import com.inventory.backend.entities.OrderReturns;
import com.inventory.backend.entities.Product;

@Repository
public class OrderReturnsDao implements IDao<OrderReturns, CustomerOrder> {

    private final JdbcTemplate jdbcTemplate;

    public OrderReturnsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(OrderReturns a) {
        String sql = "INSERT INTO orders_returned (order_id, return_date, return_reason) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, a.getOrder().getOrderId(), a.getReturnDate(), a.getReturnReason());
    }

    @Override
    public Optional<OrderReturns> findById(CustomerOrder id) {
        String sql = "SELECT ors.*, co.* FROM orders_returned ors JOIN customer_orders co ON ors.order_id = co.order_id WHERE ors.order_id = ?";
        List<OrderReturns> results = jdbcTemplate.query(sql, new OrderReturnsNormalRowMapper(), id.getOrderId());
        return results.stream().findFirst();
    }

    @Override
    public List<OrderReturns> findAll() {
        String sql = "SELECT ors.*, co.*, c.*, p.product_id, p.product_name, p.maximum_retail_price, cop.quantity " +
                     "FROM orders_returned ors " +
                     "JOIN customer_orders co ON ors.order_id = co.order_id " +
                     "JOIN customers c ON co.customer_email = c.email " +
                     "JOIN customer_orders_products cop ON co.order_id = cop.order_id " +
                     "JOIN products p ON cop.product_id = p.product_id";

        TreeMap<Long, OrderReturns> orderReturns = jdbcTemplate.query(sql, new OrderReturnsRowMapper());
        return new ArrayList<>(orderReturns.values());
    }

    @Override
    public void update(OrderReturns a, CustomerOrder id) {
        String sql = "UPDATE orders_returned SET return_date = ?, return_reason = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, a.getReturnDate(), a.getReturnReason(), id.getOrderId());
    }

    @Override
    public void delete(CustomerOrder id) {
        String sql = "DELETE FROM orders_returned WHERE order_id = ?";
        jdbcTemplate.update(sql, id.getOrderId());
    }

    public List<Long> findOrderIds() {
        String sql = "SELECT order_id FROM orders_returned";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    // RowMapper without builder
    public static class OrderReturnsNormalRowMapper implements RowMapper<OrderReturns> {
        @Override
        public OrderReturns mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setEmailId(rs.getString("customer_email"));

            Employee employee = new Employee();
            employee.setEmployeeId(rs.getLong("processed_by_employee_id"));

            CustomerOrder order = new CustomerOrder();
            order.setOrderId(rs.getLong("order_id"));
            order.setDateOfOrder(rs.getDate("date_of_order"));
            order.setCustomer(customer);
            order.setProcessorEmployee(employee);
            order.setPaymentMethod(CustomerOrder.PaymentMethod.valueOf(rs.getString("payment_method")));

            OrderReturns orderReturn = new OrderReturns();
            orderReturn.setOrder(order);
            orderReturn.setReturnDate(rs.getDate("return_date"));
            orderReturn.setReturnReason(rs.getString("return_reason"));

            return orderReturn;
        }
    }

    // ResultSetExtractor without builder
    public static class OrderReturnsRowMapper implements ResultSetExtractor<TreeMap<Long, OrderReturns>> {
        @Override
        public TreeMap<Long, OrderReturns> extractData(ResultSet rs) throws SQLException {
            TreeMap<Long, OrderReturns> orderReturns = new TreeMap<>();

            while (rs.next()) {
                Long orderId = rs.getLong("order_id");

                if (!orderReturns.containsKey(orderId)) {
                    Customer customer = new Customer();
                    customer.setEmailId(rs.getString("email"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customer.setShippingAddress(rs.getString("shipping_address"));
                    customer.setBillingAddress(rs.getString("billing_address"));

                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getLong("processed_by_employee_id"));

                    Set<CustomerOrder.Pair<Product, Integer>> products = new HashSet<>();
                    Product product = new Product();
                    product.setProductId(rs.getLong("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));
                    CustomerOrder.Pair<Product, Integer> pair = new CustomerOrder.Pair<>(product, rs.getInt("quantity"));
                    products.add(pair);

                    CustomerOrder customerOrder = new CustomerOrder();
                    customerOrder.setOrderId(orderId);
                    customerOrder.setCustomer(customer);
                    customerOrder.setProcessorEmployee(employee);
                    customerOrder.setDateOfOrder(rs.getDate("date_of_order"));
                    customerOrder.setPaymentMethod(CustomerOrder.PaymentMethod.valueOf(rs.getString("payment_method")));
                    customerOrder.setProducts(products);

                    OrderReturns orderReturn = new OrderReturns();
                    orderReturn.setOrder(customerOrder);
                    orderReturn.setReturnDate(rs.getDate("return_date"));
                    orderReturn.setReturnReason(rs.getString("return_reason"));

                    orderReturns.put(orderId, orderReturn);
                } else {
                    OrderReturns existingOrderReturn = orderReturns.get(orderId);
                    Product product = new Product();
                    product.setProductId(rs.getLong("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));
                    CustomerOrder.Pair<Product, Integer> pair = new CustomerOrder.Pair<>(product, rs.getInt("quantity"));
                    existingOrderReturn.getOrder().getProducts().add(pair);
                }
            }

            return orderReturns;
        }
    }
}
