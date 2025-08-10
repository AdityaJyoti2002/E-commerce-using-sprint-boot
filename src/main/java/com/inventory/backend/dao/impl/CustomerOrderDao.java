package com.inventory.backend.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.Employee;
import com.inventory.backend.entities.Product;
import com.inventory.backend.entities.ShippingInfoCustomerOrder;
import com.inventory.backend.services.impl.ShippingInfoCustomerOrderService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



@Repository
public class CustomerOrderDao implements IDao<CustomerOrder, Long> {

    private final JdbcTemplate jdbcTemplate;

    private ShippingInfoCustomerOrderService shippingInfoCustomerOrderService;

    public CustomerOrderDao(JdbcTemplate jdbcTemplate,
                                        ShippingInfoCustomerOrderService shippingInfoCustomerOrderService) {
        this.jdbcTemplate = jdbcTemplate;
        this.shippingInfoCustomerOrderService = shippingInfoCustomerOrderService;
    }

    @Override
    public void create(CustomerOrder customerOrder) {
        String sql = "INSERT INTO customer_orders (date_of_order, customer_email, processed_by_employee_id, payment_method) VALUES (?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, customerOrder.getDateOfOrder());
            preparedStatement.setString(2, customerOrder.getCustomer().getEmailId());
            if (customerOrder.getProcessorEmployee() == null) {
                preparedStatement.setNull(3, java.sql.Types.BIGINT);
            } else {
                preparedStatement.setLong(3, customerOrder.getProcessorEmployee().getEmployeeId());
            }
            preparedStatement.setString(4, customerOrder.getPaymentMethod().name());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0)
            {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next() && customerOrder.getProducts() != null)
                {
                    int insertId = generatedKeys.getInt(1);
                    
                    String sql2 = "INSERT INTO customer_orders_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
                    jdbcTemplate.batchUpdate(sql2, customerOrder.getProducts().stream().map(pair -> new Object[]{insertId, pair.getFirst().getProductId(), pair.getSecond()}).toList());

                    customerOrder.setOrderId((long) insertId);

                    ShippingInfoCustomerOrder shippingInfoCustomerOrder = new ShippingInfoCustomerOrder();
                                                shippingInfoCustomerOrder.setOrder(customerOrder);
                                                shippingInfoCustomerOrder.setShippingDate(null);
                                                shippingInfoCustomerOrder.setExpectedDeliveryDate(null);
                                                shippingInfoCustomerOrder.setStatus(ShippingInfoCustomerOrder.Status.PENDING);


                    shippingInfoCustomerOrderService.save(shippingInfoCustomerOrder);
                }


            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Optional<CustomerOrder> findById(Long id) {
        String sql = "SELECT co.*, c.*, e.first_name as e_first_name, e.last_name as e_last_name, p.product_id, p.product_name, p.maximum_retail_price, cop.quantity FROM customer_orders co LEFT JOIN customers c ON co.customer_email = c.email LEFT JOIN employees e ON co.processed_by_employee_id = e.employee_id LEFT JOIN customer_orders_products cop ON co.order_id = cop.order_id LEFT JOIN products p ON cop.product_id = p.product_id WHERE co.order_id = ?";
        TreeMap <Long, CustomerOrder> customerOrderMap = jdbcTemplate.query(sql, new CustomerOrderRowMapper(), id);
        return Optional.ofNullable(customerOrderMap.get(id));
    }

    @Override
    public List<CustomerOrder> findAll() {
        String sql = "SELECT co.*, c.*, e.first_name as e_first_name, e.last_name as e_last_name, p.product_id, p.product_name, p.maximum_retail_price, cop.quantity FROM customer_orders co LEFT JOIN customers c ON co.customer_email = c.email LEFT JOIN employees e ON co.processed_by_employee_id = e.employee_id LEFT JOIN customer_orders_products cop ON co.order_id = cop.order_id LEFT JOIN products p ON cop.product_id = p.product_id ORDER BY co.order_id";
        TreeMap <Long, CustomerOrder> customerOrderMap = jdbcTemplate.query(sql, new CustomerOrderRowMapper());
        return new ArrayList<>(customerOrderMap.values());
    }
       public List<CustomerOrder> findOrdersByCustomerEmail(String customerEmail) {
        String sql = "SELECT co.*, c.*, e.first_name as e_first_name, e.last_name as e_last_name, p.product_id, p.product_name, p.maximum_retail_price, cop.quantity " +
                     "FROM customer_orders co " +
                     "LEFT JOIN customers c ON co.customer_email = c.email " +
                     "LEFT JOIN employees e ON co.processed_by_employee_id = e.employee_id " +
                     "LEFT JOIN customer_orders_products cop ON co.order_id = cop.order_id " +
                     "LEFT JOIN products p ON cop.product_id = p.product_id " +
                     "WHERE c.email = ?";

        Map<Long, CustomerOrder> customerOrderMap = jdbcTemplate.query(sql, new CustomerOrderRowMapper(), customerEmail);
        return List.copyOf(customerOrderMap.values());
    }

    
    @Override
    public void update(CustomerOrder customerOrder, Long id) {
        String sql = "UPDATE customer_orders SET date_of_order = ?, customer_email = ?, processed_by_employee_id = ?, payment_method = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, customerOrder.getDateOfOrder(), customerOrder.getCustomer().getEmailId(), customerOrder.getProcessorEmployee().getEmployeeId(), customerOrder.getPaymentMethod().name(), id);
        sql = "DELETE FROM customer_orders_products WHERE order_id = ?";
        jdbcTemplate.update(sql, id);
        if (customerOrder.getProducts() != null) {
            sql = "INSERT INTO customer_orders_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
            jdbcTemplate.batchUpdate(sql, customerOrder.getProducts().stream().map(pair -> new Object[]{id, pair.getFirst().getProductId(), pair.getSecond()}).toList());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM customer_orders WHERE order_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // total sales grouped by category
    public Map<String, Double> totalSalesByCategory() {
        String sql = "SELECT c.category_name, SUM(p.maximum_retail_price * cop.quantity) as total_sales FROM customer_orders co JOIN customer_orders_products cop ON co.order_id = cop.order_id JOIN products p ON cop.product_id = p.product_id JOIN categories c ON p.category_id = c.category_id GROUP BY c.category_name LIMIT 5";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Double>>() {
            @Override
            public Map<String, Double> extractData(ResultSet rs) throws SQLException {
                Map<String, Double> totalSalesByCategory = new HashMap<>();
                while (rs.next()) {
                    totalSalesByCategory.put(rs.getString("category_name"), rs.getDouble("total_sales"));
                }
                return totalSalesByCategory;
            }
        });
    }

    public static class CustomerOrderRowMapper implements ResultSetExtractor<TreeMap<Long, CustomerOrder>>
    {
        @Override
        public TreeMap<Long, CustomerOrder> extractData(ResultSet rs) throws SQLException
        {
            TreeMap<Long, CustomerOrder> customerOrderMap = new TreeMap<>();

        

            while(rs.next()){ 
                Long customerOrderId = rs.getLong("order_id");

                if (!customerOrderMap.containsKey(customerOrderId))
                {
                    Customer customer = new Customer();
                        customer.setEmailId(rs.getString("customer_email"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setPhoneNumber(rs.getString("phone_number"));
                        customer.setShippingAddress(rs.getString("shipping_address"));
                        customer.setBillingAddress(rs.getString("billing_address"));

                    Employee employee = new Employee();
                        employee.setEmployeeId(rs.getLong("processed_by_employee_id"));
                        employee.setFirstName(rs.getString("e_first_name"));
                        employee.setLastName(rs.getString("e_last_name"));

                    CustomerOrder.PaymentMethod paymentMethod = CustomerOrder.PaymentMethod.valueOf(rs.getString("payment_method"));

                    Set<CustomerOrder.Pair<Product, Integer>> products = new HashSet<>();
                    Product product = new Product();
                            product.setProductId(rs.getLong("product_id"));
                            product.setProductName(rs.getString("product_name"));
                            product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));

                            // Create Pair object (Product, Quantity)
                    CustomerOrder.Pair<Product, Integer> productPair = new CustomerOrder.Pair<>(product, rs.getInt("quantity"));

                    // Create products set and add the product pair
                    // Set<CustomerOrder.Pair<Product, Integer>> products = new HashSet<>();
                    products.add(productPair);

                    CustomerOrder customerOrder = new CustomerOrder();
                    customerOrder.setOrderId(customerOrderId);
                    customerOrder.setCustomer(customer); // already constructed above
                    customerOrder.setDateOfOrder(rs.getDate("date_of_order"));
                    customerOrder.setProcessorEmployee(employee); // already constructed above
                    customerOrder.setPaymentMethod(paymentMethod);
                    customerOrder.setProducts(products);
                    

                    customerOrderMap.put(customerOrderId, customerOrder);
                }
                else
                {
                    Product product = new Product();
                            product.setProductId(rs.getLong("product_id"));
                            product.setProductName(rs.getString("product_name"));
                            product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));

                            CustomerOrder.Pair<Product, Integer> productPair = new CustomerOrder.Pair<>(product, rs.getInt("quantity"));

                            customerOrderMap.get(customerOrderId).getProducts().add(productPair);
                }
            }

            return customerOrderMap;
        }
    }
    
}
