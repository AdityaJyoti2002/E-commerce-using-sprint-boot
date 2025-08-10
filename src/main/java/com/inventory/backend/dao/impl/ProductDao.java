package com.inventory.backend.dao.impl;

import java.util.*;
import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Category;
import com.inventory.backend.entities.Manufacturer;
import com.inventory.backend.entities.Product;

@Repository
public class ProductDao implements IDao<Product, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Product a) {
        String sql = "INSERT INTO products (product_name, expiry_date, stock_quantity, selling_price, maximum_retail_price, category_id, description, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, a.getProductName());
            preparedStatement.setDate(2, a.getExpiryDate());
            preparedStatement.setInt(3, a.getStockQuantity());
            preparedStatement.setDouble(4, a.getSellingPrice());
            preparedStatement.setDouble(5, a.getMaximumRetailPrice());
            preparedStatement.setLong(6, a.getCategory().getCategoryId());
            preparedStatement.setString(7, a.getDescription());
            preparedStatement.setString(8, a.getImageUrl());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int insertId = generatedKeys.getInt(1);
                    String sql2 = "INSERT INTO product_manufacturers (product_id, manufacturer_id, cost_price) VALUES (?, ?, ?)";
                    jdbcTemplate.batchUpdate(sql2, a.getManufacturers().stream().map(pair -> new Object[]{pair.getFirst().getManufacturerId(), pair.getSecond(), insertId}).toList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT p.*, c.*, m.*, pm.cost_price FROM products p JOIN categories c ON p.category_id = c.category_id JOIN product_manufacturers pm ON p.product_id = pm.product_id JOIN manufacturers m ON pm.manufacturer_id = m.manufacturer_id WHERE p.product_id = ?";
        Map<Long, Product> productMap = jdbcTemplate.query(sql, new ProductRowMapper(), id);
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT p.*, c.*, m.*, pm.cost_price FROM products p JOIN categories c ON p.category_id = c.category_id JOIN product_manufacturers pm ON p.product_id = pm.product_id JOIN manufacturers m ON pm.manufacturer_id = m.manufacturer_id";
        Map<Long, Product> productMap = jdbcTemplate.query(sql, new ProductRowMapper());
        return new ArrayList<>(productMap.values());
    }

    @Override
    public void update(Product a, Long id) {
        String sql = "UPDATE products SET product_name = ?, expiry_date = ?, stock_quantity = ?, selling_price = ?, maximum_retail_price = ?, category_id = ?, description = ?, image_url = ? WHERE product_id = ?";
        String sql2 = "DELETE FROM product_manufacturers WHERE product_id = ?";
        String sql3 = "INSERT INTO product_manufacturers (product_id, manufacturer_id, cost_price) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql2, id);

        if (a.getImageUrl() != null) {
            deletePreviousImages(id);
            jdbcTemplate.update(sql, a.getProductName(), a.getExpiryDate(), a.getStockQuantity(), a.getSellingPrice(), a.getMaximumRetailPrice(), a.getCategory().getCategoryId(), a.getDescription(), a.getImageUrl(), id);
        } else {
            sql = "UPDATE products SET product_name = ?, expiry_date = ?, stock_quantity = ?, selling_price = ?, maximum_retail_price = ?, category_id = ?, description = ? WHERE product_id = ?";
            jdbcTemplate.update(sql, a.getProductName(), a.getExpiryDate(), a.getStockQuantity(), a.getSellingPrice(), a.getMaximumRetailPrice(), a.getCategory().getCategoryId(), a.getDescription(), id);
        }

        for (Product.Pair<Manufacturer, Double> pair : a.getManufacturers()) {
            jdbcTemplate.update(sql3, id, pair.getFirst().getManufacturerId(), pair.getSecond());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deletePreviousImages(Long id) {
        String sql = "SELECT image_url FROM products WHERE product_id = ?";
        try {
            String imageUrl = jdbcTemplate.queryForObject(sql, String.class, id);
            Path path = Path.of("src/main/resources/static/images/" + imageUrl);
            Files.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Product> findDealsOfTheDay() {
        String sql = "SELECT * FROM products WHERE stock_quantity > 0 ORDER BY selling_price LIMIT 5";
        return jdbcTemplate.query(sql, new ProductSimpleMapper());
    }

    public List<Product> getByCategory(Long categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ?";
        return jdbcTemplate.query(sql, new ProductSimpleMapper(), categoryId);
    }

    public List<Product> getByCategory(Long categoryId, int limit) {
        String sql = "SELECT * FROM products WHERE category_id = ? LIMIT ?";
        return jdbcTemplate.query(sql, new ProductSimpleMapper(), categoryId, limit);
    }

    public void updateProductQuantity(Long productId, int delta) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        jdbcTemplate.update(sql, delta, productId);
    }

    public Map<String, Integer> stockQuantityByCategory() {
        String sql = "SELECT c.category_name, SUM(p.stock_quantity) AS total_quantity FROM products p JOIN categories c ON p.category_id = c.category_id GROUP BY c.category_name";
        return jdbcTemplate.query(sql, rs -> {
            Map<String, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getInt("total_quantity"));
            }
            return map;
        });
    }

    public List<Product> searchProducts(String keyword) {
        String sql = "SELECT * FROM products WHERE LOWER(product_name) LIKE ?";
        return jdbcTemplate.query(sql, new ProductSimpleMapper(), "%" + keyword.toLowerCase() + "%");
    }

    public static class ProductRowMapper implements ResultSetExtractor<Map<Long, Product>> {
        @Override
        public Map<Long, Product> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Product> productMap = new HashMap<>();
            while (rs.next()) {
                Long productId = rs.getLong("product_id");
                Product product = productMap.get(productId);
                if (product == null) {
                    Category category = new Category();
                    category.setCategoryId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    category.setCategoryDescription(rs.getString("category_description"));

                    product = new Product();
                    product.setProductId(productId);
                    product.setProductName(rs.getString("product_name"));
                    product.setExpiryDate(rs.getDate("expiry_date"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setDescription(rs.getString("description"));
                    product.setSellingPrice(rs.getDouble("selling_price"));
                    product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));
                    product.setCategory(category);
                    product.setImageUrl(rs.getString("image_url"));
                    product.setManufacturers(new HashSet<>());
                    productMap.put(productId, product);
                }

                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManufacturerId(rs.getLong("manufacturer_id"));
                manufacturer.setManufacturerName(rs.getString("manufacturer_name"));

                Double costPrice = rs.getDouble("cost_price");
                product.getManufacturers().add(new Product.Pair<>(manufacturer, costPrice));
            }
            return productMap;
        }
    }

    public static class ProductSimpleMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setProductId(rs.getLong("product_id"));
            product.setProductName(rs.getString("product_name"));
            product.setSellingPrice(rs.getDouble("selling_price"));
            product.setStockQuantity(rs.getInt("stock_quantity"));
            product.setImageUrl(rs.getString("image_url"));
            return product;
        }
    }

    public class Pair<T, U> {
        private final T first;
        private final U second;
    
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    
        public T getFirst() {
            return first;
        }
    
        public U getSecond() {
            return second;
        }
    }
}
