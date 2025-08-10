package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.ManufacturerDao;
import com.inventory.backend.entities.Manufacturer;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ManufacturerService implements IModelService<Manufacturer, Long> {

    private ManufacturerDao manufacturerDao;

    public ManufacturerService(ManufacturerDao manufacturerDao) {
        this.manufacturerDao = manufacturerDao;
    }

    @Override
    public Optional<Manufacturer> save(Manufacturer manufacturer) {
        manufacturerDao.create(manufacturer);
        return Optional.of(manufacturer);
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        return manufacturerDao.findById(id);
    }

    @Override
    public List<Manufacturer> findAll() {
        return manufacturerDao.findAll();
    }

    @Override
    public Optional<Manufacturer> update(Manufacturer manufacturer, Long id) {
        manufacturerDao.update(manufacturer, id);
        return Optional.of(manufacturer);
    }

    @Override
    public void delete(Long id) {
        manufacturerDao.delete(id);
    }
    
}
