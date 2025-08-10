package com.inventory.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.ManufacturerOrder;
import com.inventory.backend.entities.ShippingInfoManufacturerOrder;

@Repository
public class ShippingInfoManufacturerOrderDao implements IDao<ShippingInfoManufacturerOrder, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ShippingInfoManufacturerOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(ShippingInfoManufacturerOrder a) {
        String sql = "INSERT INTO manufacturer_order_shipping_info (shipping_date, expected_delivery_date, status, manufacturer_order_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, a.getShippingDate(), a.getExpectedDeliveryDate(), a.getStatus().name(), a.getManufacturerOrder().getOrderId());
    }

    @Override
    public Optional<ShippingInfoManufacturerOrder> findById(Long id) {
        String sql = "SELECT simo.*, mo.* FROM manufacturer_order_shipping_info simo JOIN manufacturer_orders mo ON simo.manufacturer_order_id = mo.order_id WHERE simo.shipping_info_id = ?";
        List<ShippingInfoManufacturerOrder> list = jdbcTemplate.query(sql, new ShippingInfoManufacturerOrderRowMapper(), id);
        return list.stream().findFirst();
    }

    @Override
    public List<ShippingInfoManufacturerOrder> findAll() {
        String sql = "SELECT simo.*, mo.* FROM manufacturer_order_shipping_info simo JOIN manufacturer_orders mo ON simo.manufacturer_order_id = mo.order_id";
        return jdbcTemplate.query(sql, new ShippingInfoManufacturerOrderRowMapper());
    }

    @Override
    public void update(ShippingInfoManufacturerOrder a, Long id) {
        String sql = "UPDATE manufacturer_order_shipping_info SET shipping_date = ?, expected_delivery_date = ?, status = ? WHERE shipping_info_id = ?";
        jdbcTemplate.update(sql, a.getShippingDate(), a.getExpectedDeliveryDate(), a.getStatus().name(), id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM manufacturer_order_shipping_info WHERE shipping_info_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<ShippingInfoManufacturerOrder> findByOrderId(Long orderId) {
        String sql = "SELECT simo.*, mo.* FROM manufacturer_order_shipping_info simo LEFT JOIN manufacturer_orders mo ON simo.manufacturer_order_id = mo.order_id WHERE simo.manufacturer_order_id = ?";
        List<ShippingInfoManufacturerOrder> results = jdbcTemplate.query(sql, new ShippingInfoManufacturerOrderRowMapper(), orderId);
        if (results.isEmpty()) {
            ManufacturerOrder order = new ManufacturerOrder();
            order.setOrderId(orderId);

            ShippingInfoManufacturerOrder newShippingInfo = new ShippingInfoManufacturerOrder();
            newShippingInfo.setManufacturerOrder(order);
            newShippingInfo.setShippingDate(null);
            newShippingInfo.setExpectedDeliveryDate(null);
            newShippingInfo.setStatus(ShippingInfoManufacturerOrder.Status.PENDING);
            create(newShippingInfo);

            results = jdbcTemplate.query(sql, new ShippingInfoManufacturerOrderRowMapper(), orderId);
        }
        return results.stream().findFirst();
    }

    public Map<Long, String> idStatusMap() {
        String sql = "SELECT manufacturer_order_id, status FROM manufacturer_order_shipping_info";
        return jdbcTemplate.query(sql, new IdStatusRowMapper())
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static class IdStatusRowMapper implements RowMapper<Map.Entry<Long, String>> {
        @Override
        public Map.Entry<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AbstractMap.SimpleEntry<>(rs.getLong("manufacturer_order_id"), rs.getString("status"));
        }
    }

    public static class ShippingInfoManufacturerOrderRowMapper implements RowMapper<ShippingInfoManufacturerOrder> {
        @Override
        public ShippingInfoManufacturerOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            ManufacturerOrder order = new ManufacturerOrder();
            order.setOrderId(rs.getLong("manufacturer_order_id"));
            order.setDateOfOrder(rs.getDate("date_of_order"));

            ShippingInfoManufacturerOrder shippingInfo = new ShippingInfoManufacturerOrder();
            shippingInfo.setManufacturerOrder(order);
            shippingInfo.setShippingInfoId(rs.getLong("shipping_info_id"));
            shippingInfo.setShippingDate(rs.getDate("shipping_date"));
            shippingInfo.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
            shippingInfo.setStatus(ShippingInfoManufacturerOrder.Status.valueOf(rs.getString("status")));

            return shippingInfo;
        }
    }
}