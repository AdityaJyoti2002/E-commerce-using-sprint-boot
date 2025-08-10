package com.inventory.backend.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Manufacturer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ManufacturerDao implements IDao<Manufacturer, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ManufacturerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Manufacturer manufacturer) {
        String sql = "INSERT INTO manufacturers (manufacturer_name, manufacturer_address) VALUES (?, ?)";
        int insertId = -1;
        try{
            PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, manufacturer.getManufacturerName());
            preparedStatement.setString(2, manufacturer.getAddress());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0)
            {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next())
                {

                    insertId = generatedKeys.getInt(1);
                    String sql2 = "INSERT INTO manufacturer_email_addresses (manufacturer_id, email_address) VALUES (?, ?)";
                    for (String email : manufacturer.getEmailIds()) {
                        jdbcTemplate.update(sql2, insertId, email);
                    }
        
                    String sql3 = "INSERT INTO manufacturer_phone_numbers (manufacturer_id, phone_number) VALUES (?, ?)";
                    for (String phone : manufacturer.getContactNumbers()) {
                        jdbcTemplate.update(sql3, insertId, phone);
                    }
                }

            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        String sql = "SELECT m.*, me.email_address, mp.phone_number FROM manufacturers m LEFT JOIN manufacturer_email_addresses me ON m.manufacturer_id = me.manufacturer_id LEFT JOIN manufacturer_phone_numbers mp ON m.manufacturer_id = mp.manufacturer_id WHERE m.manufacturer_id = ?";
        List<Manufacturer> results = jdbcTemplate.query(sql, new ManufacturerRowMapper(), id);
        return results.stream().findFirst();
    }

    @Override
    public List<Manufacturer> findAll() {
        String sql = "SELECT m.*, me.email_address, mp.phone_number FROM manufacturers m LEFT JOIN manufacturer_email_addresses me ON m.manufacturer_id = me.manufacturer_id LEFT JOIN manufacturer_phone_numbers mp ON m.manufacturer_id = mp.manufacturer_id";
        return jdbcTemplate.query(sql, new ManufacturerRowMapper());
    }

    @Override
    public void update(Manufacturer manufacturer, Long id) {
        String sql = "UPDATE manufacturers SET manufacturer_name = ?, manufacturer_address = ? WHERE manufacturer_id = ?";
        jdbcTemplate.update(sql, manufacturer.getManufacturerName(), manufacturer.getAddress(), id);
        sql = "DELETE FROM manufacturer_email_addresses WHERE manufacturer_id = ?";
        jdbcTemplate.update(sql, id);
        if (manufacturer.getEmailIds() != null) {
            sql = "INSERT INTO manufacturer_email_addresses (manufacturer_id, email_address) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, manufacturer.getEmailIds().stream().map(email -> new Object[]{id, email}).toList());
        }
        sql = "DELETE FROM manufacturer_phone_numbers WHERE manufacturer_id = ?";
        jdbcTemplate.update(sql, id);
        if (manufacturer.getContactNumbers() != null) {
            sql = "INSERT INTO manufacturer_phone_numbers (manufacturer_id, phone_number) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, manufacturer.getContactNumbers().stream().map(phone -> new Object[]{id, phone}).toList());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM manufacturers WHERE manufacturer_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public static class ManufacturerRowMapper implements ResultSetExtractor<List<Manufacturer>> {
        @Override
        public List<Manufacturer> extractData(ResultSet rs) throws SQLException {
            
            Map <Long, Manufacturer> manufacturerMap = new HashMap<>();
            while (rs.next()) {
                Long manufacturerId = rs.getLong("manufacturer_id");
                Manufacturer manufacturer = manufacturerMap.get(manufacturerId);
                if (manufacturer == null) {
                    manufacturer = new Manufacturer();
                    manufacturer.setManufacturerId(manufacturerId);
                    manufacturer.setManufacturerName(rs.getString("manufacturer_name"));
                    manufacturer.setAddress(rs.getString("manufacturer_address"));
                    manufacturer.setEmailIds(new HashSet<>());
                    manufacturer.setContactNumbers(new HashSet<>());
                    
                    manufacturerMap.put(manufacturerId, manufacturer);
                    
                }
                String email = rs.getString("email_address");
                if (email != null) {
                    manufacturer.getEmailIds().add(email);
                }
                String phone = rs.getString("phone_number");
                if (phone != null) {
                    manufacturer.getContactNumbers().add(phone);
                }
            }
            return new ArrayList<>(manufacturerMap.values());


                                                
    }
}
    
}
