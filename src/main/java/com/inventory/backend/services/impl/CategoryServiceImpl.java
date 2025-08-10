package com.inventory.backend.services.impl;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Category;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements IModelService<Category, Long> {

    private IDao<Category, Long> categoryDao;

    public CategoryServiceImpl(IDao<Category, Long> categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Optional<Category> save(Category category) {
        categoryDao.create(category);
        return Optional.of(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryDao.findById(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public Optional<Category> update(Category category, Long id) {
        categoryDao.update(category, id);
        return Optional.of(category);
    }

    @Override
    public void delete(Long id) {
        categoryDao.delete(id);
    }
    
}
