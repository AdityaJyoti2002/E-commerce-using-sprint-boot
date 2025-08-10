package com.inventory.backend.dao.impl;


import java.util.Optional;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Category;

@Repository
public class CategoryDaoImpl implements IDao<Category, Long>{
    
    private final JdbcTemplate jdbcTemplate;
    
    public CategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Category category) {
        String sql = "INSERT INTO categories (category_name, category_description) VALUES (?, ?)";
        jdbcTemplate.update(sql, category.getCategoryName(), category.getCategoryDescription());
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        List<Category> results = jdbcTemplate.query(sql, new CategoryRowMapper(), id);
        return results.stream().findFirst();
    }

    public static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Category category = new Category();
            category.setCategoryId(rs.getLong("category_id"));
            category.setCategoryName(rs.getString("category_name"));
            category.setCategoryDescription(rs.getString("category_description"));
            return category;
        }
        
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }

    @Override
    public void update(Category a, Long id) {
        String sql = "UPDATE categories SET category_name = ?, category_description = ? WHERE category_id = ?";
        jdbcTemplate.update(sql, a.getCategoryName(), a.getCategoryDescription(), id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        jdbcTemplate.update(sql, id);
    }

}
