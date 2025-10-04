package com.inventory.backend.dao.impl;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.embeddable.SalesReportCompositeKey;
import com.inventory.backend.entities.Category;
import com.inventory.backend.entities.Product;
import com.inventory.backend.entities.SalesReport;

@Repository
public class SalesReportDao implements IDao<SalesReport, SalesReportCompositeKey> {

    private final JdbcTemplate jdbcTemplate;

    public SalesReportDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    // public void create(SalesReport a) {
    //     String sql = "INSERT INTO sales_report (day, month, year, total_sales, total_orders, top_selling_product_id) VALUES (?, ?, ?, ?, ?, ?)";
    //     a.setTopSellingProduct(findTopSellingProduct(a.getSalesReportCompositeKey()));
    //     jdbcTemplate.update(sql,
    //             a.getSalesReportCompositeKey().getDay(),
    //             a.getSalesReportCompositeKey().getMonth(),
    //             a.getSalesReportCompositeKey().getYear(),
    //             a.getTotalSales(),
    //             a.getTotalOrders(),
    //             a.getTopSellingProduct().getProductId());
    // }
    public void create(SalesReport a) {
        // Find top selling product (may return null)
        Product topProduct = findTopSellingProduct(a.getSalesReportCompositeKey());
    
        // Set top selling product if exists
        a.setTopSellingProduct(topProduct);
    
        String sql = "INSERT INTO sales_report " +
                     "(day, month, year, total_sales, total_orders, top_selling_product_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
    
        jdbcTemplate.update(sql,
                a.getSalesReportCompositeKey().getDay(),
                a.getSalesReportCompositeKey().getMonth(),
                a.getSalesReportCompositeKey().getYear(),
                a.getTotalSales(),
                a.getTotalOrders(),
                topProduct != null ? topProduct.getProductId() : null // <-- safe handling
        );
    }
    

    private Product findTopSellingProduct(SalesReportCompositeKey key) {
        String sql = "SELECT p.*, c.*, m.*, pm.cost_price FROM products p " +
                     "LEFT JOIN categories c ON c.category_id = p.category_id " +
                     "LEFT JOIN product_manufacturers pm ON p.product_id = pm.product_id " +
                     "JOIN manufacturers m ON pm.manufacturer_id = m.manufacturer_id " +
                     "WHERE p.product_id = (" +
                     "SELECT product_id FROM customer_orders_products " +
                     "WHERE order_id IN (" +
                     "SELECT order_id FROM customer_orders WHERE date_of_order = DATE_FORMAT(?, '%Y-%m-%d')) " +
                     "GROUP BY product_id ORDER BY SUM(quantity) DESC LIMIT 1)";

        Map<Long, Product> productMap = jdbcTemplate.query(sql, new ProductDao.ProductRowMapper(),
                Date.valueOf(key.getYear() + "-" + key.getMonth() + "-" + key.getDay()));

        if (productMap.isEmpty()) {
            Product product = new Product();
            product.setProductId(0L);
            return product;
        }
        return productMap.values().iterator().next();
    }

    @Override
    public Optional<SalesReport> findById(SalesReportCompositeKey id) {
        String sql = "SELECT sr.*, p.* FROM sales_report sr LEFT JOIN products p ON sr.top_selling_product_id = p.product_id WHERE sr.day = ? AND sr.month = ? AND sr.year = ?";
        List<SalesReport> salesReports = jdbcTemplate.query(sql, new SalesReportRowMapper(), id.getDay(), id.getMonth(), id.getYear());
        return salesReports.isEmpty() ? Optional.empty() : Optional.of(salesReports.get(0));
    }

    @Override
    public List<SalesReport> findAll() {
        String sql = "SELECT sr.*, p.* FROM sales_report sr LEFT JOIN products p ON sr.top_selling_product_id = p.product_id";
        return jdbcTemplate.query(sql, new SalesReportRowMapper());
    }

    @Override
    // public void update(SalesReport a, SalesReportCompositeKey id) {
    //     String sql = "UPDATE sales_report SET total_sales = ?, total_orders = ?, top_selling_product_id = ? WHERE day = ? AND month = ? AND year = ?";
    //     a.setTopSellingProduct(findTopSellingProduct(id));
    //     jdbcTemplate.update(sql,
    //             a.getTotalSales(),
    //             a.getTotalOrders(),
    //             a.getTopSellingProduct().getProductId(),
    //             id.getDay(),
    //             id.getMonth(),
    //             id.getYear());
    // }
    public void update(SalesReport a, SalesReportCompositeKey id) {
        // Find top selling product (may return null)
        Product topProduct = findTopSellingProduct(id);
        a.setTopSellingProduct(topProduct);
    
        String sql = "UPDATE sales_report SET total_sales = ?, total_orders = ?, top_selling_product_id = ? " +
                     "WHERE day = ? AND month = ? AND year = ?";
    
        jdbcTemplate.update(sql,
                a.getTotalSales(),
                a.getTotalOrders(),
                // If product exists in DB, set its ID, otherwise null
                (topProduct != null && productExists(topProduct.getProductId())) ? topProduct.getProductId() : null,
                id.getDay(),
                id.getMonth(),
                id.getYear()
        );
    }
    
    // Helper to check if product exists in products table
    private boolean productExists(Long productId) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productId);
        return count != null && count > 0;
    }
    

    @Override
    public void delete(SalesReportCompositeKey id) {
        String sql = "DELETE FROM sales_report WHERE day = ? AND month = ? AND year = ?";
        jdbcTemplate.update(sql, id.getDay(), id.getMonth(), id.getYear());
    }

    public List<SalesReport> weeklySales(Date start, Date end) {
        String sql = "SELECT sr.*, p.* FROM sales_report sr LEFT JOIN products p ON sr.top_selling_product_id = p.product_id WHERE DATE_FORMAT(CONCAT(sr.year, '-', sr.month, '-', sr.day), '%Y-%m-%d') BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new SalesReportRowMapper(), start, end);
    }

    public static class SalesReportRowMapper implements RowMapper<SalesReport> {
        @Override
        public SalesReport mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            SalesReportCompositeKey key = new SalesReportCompositeKey();
            key.setDay(rs.getInt("day"));
            key.setMonth(rs.getInt("month"));
            key.setYear(rs.getInt("year"));

            Product product = new Product();
            product.setProductId(rs.getLong("product_id"));
            product.setProductName(rs.getString("product_name"));
            product.setExpiryDate(rs.getDate("expiry_date"));
            product.setStockQuantity(rs.getInt("stock_quantity"));
            product.setSellingPrice(rs.getDouble("selling_price"));
            product.setMaximumRetailPrice(rs.getDouble("maximum_retail_price"));

            Category category = new Category();
            category.setCategoryId(rs.getLong("category_id"));
            product.setCategory(category);
            product.setManufacturers(new HashSet<>());

            SalesReport report = new SalesReport();
            report.setSalesReportCompositeKey(key);
            report.setTotalSales(rs.getDouble("total_sales"));
            report.setTotalOrders(rs.getInt("total_orders"));
            report.setTopSellingProduct(product);

            return report;
        }
    }
}
