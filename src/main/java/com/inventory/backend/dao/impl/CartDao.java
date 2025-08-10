package com.inventory.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


import org.apache.commons.lang3.tuple.Triple;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Cart;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.Product;

@Repository
public class CartDao implements IDao<Cart, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CartDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Cart a) {
        String sql = "INSERT INTO carts (customer_email) VALUES (?)";
        jdbcTemplate.update(sql, a.getCustomer().getEmailId());
    }

    @Override
    public Optional<Cart> findById(Long id) {
        String sql = "SELECT c.*, p.*, cp.* FROM carts c " +
                     "JOIN cart_products cp ON c.cart_id = cp.cart_id " +
                     "JOIN products p ON cp.product_id = p.product_id WHERE c.cart_id = ?";
        Map<Long, Cart> cartMap = jdbcTemplate.query(sql, new CartRowMapper(), id);
        return Optional.ofNullable(cartMap.get(id));
    }

    @Override
    public List<Cart> findAll() {
        String sql = "SELECT c.*, p.*, cp.* FROM carts c " +
                     "JOIN cart_products cp ON c.cart_id = cp.cart_id " +
                     "JOIN products p ON cp.product_id = p.product_id";
        Map<Long, Cart> cartMap = jdbcTemplate.query(sql, new CartRowMapper());
        return new ArrayList<>(cartMap.values());
    }

    @Override
    public void update(Cart a, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM carts WHERE cart_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Boolean isProductInCart(Long cartId, Long productId) {
        String sql = "SELECT COUNT(*) FROM cart_products WHERE cart_id = ? AND product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cartId, productId);
        return count != null && count > 0;
    }

    public void addProduct(Long cartId, Long productId, Integer quantity) {
        if (isProductInCart(cartId, productId)) {
            String updateSql = "UPDATE cart_products SET quantity = ? WHERE cart_id = ? AND product_id = ?";
            jdbcTemplate.update(updateSql, quantity, cartId, productId);
        } else {
            String insertSql = "INSERT INTO cart_products (cart_id, product_id, quantity, added_on) VALUES (?, ?, ?, NOW())";
            jdbcTemplate.update(insertSql, cartId, productId, quantity);
        }
    }

    public void removeProduct(Long cartId, Long productId) {
        String sql = "DELETE FROM cart_products WHERE cart_id = ? AND product_id = ?";
        jdbcTemplate.update(sql, cartId, productId);
    }

    public Long getCartIdByCustomerEmail(String email) {
        String sql = "SELECT cart_id FROM carts WHERE customer_email = ?";
        List<Long> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("cart_id"), email);
        return results.isEmpty() ? null : results.get(0);
    }

    public static class CartRowMapper implements ResultSetExtractor<Map<Long, Cart>> {

        @Override
        public Map<Long, Cart> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Cart> cartMap = new HashMap<>();

            while (rs.next()) {
                Long cartId = rs.getLong("cart_id");

                Cart cart = cartMap.computeIfAbsent(cartId, id -> {
                    Cart newCart = new Cart();
                    newCart.setCartId(id);

                    Customer customer = new Customer();
                        try {
                            customer.setEmailId(rs.getString("customer_email"));
                            // You can set other fields here too
                        } catch (SQLException e) {
                            e.printStackTrace(); // or use proper logging
                        }  // ✅ CORRECT
  // using a safe setter if needed
                    newCart.setCustomer(customer);

                    newCart.setProducts(new HashSet<>());
                    return newCart;
                });

                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setSellingPrice(rs.getDouble("selling_price"));
                product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));

                Triple<Product, Integer, java.sql.Date> triple = Triple.of(product, rs.getInt("quantity"), rs.getDate("added_on"));
                cart.getProducts().add(triple);
            }

            return cartMap;
        }
    }
}
