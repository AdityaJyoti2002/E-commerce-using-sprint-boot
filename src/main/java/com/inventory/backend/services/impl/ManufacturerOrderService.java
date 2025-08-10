package com.inventory.backend.services.impl;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.ManufacturerOrder;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ManufacturerOrderService implements IModelService<ManufacturerOrder, Long> {

    private IDao<ManufacturerOrder, Long> manufacturerOrderDao;

    public ManufacturerOrderService(IDao<ManufacturerOrder, Long> manufacturerOrderDao) {
        this.manufacturerOrderDao = manufacturerOrderDao;
    }

    @Override
    public Optional<ManufacturerOrder> save(ManufacturerOrder manufacturerOrder) {
        manufacturerOrderDao.create(manufacturerOrder);
        return Optional.of(manufacturerOrder);
    }

    @Override
    public Optional<ManufacturerOrder> findById(Long id) {
        return manufacturerOrderDao.findById(id);
    }

    @Override
    public List<ManufacturerOrder> findAll() {
        return manufacturerOrderDao.findAll();
    }

    @Override
    public Optional<ManufacturerOrder> update(ManufacturerOrder manufacturerOrder, Long id) {
        manufacturerOrderDao.update(manufacturerOrder, id);
        return Optional.of(manufacturerOrder);
    }

    @Override
    public void delete(Long id) {
        manufacturerOrderDao.delete(id);
    }
    
}
