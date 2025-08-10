package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.ShippingInfoCustomerOrderDao;
import com.inventory.backend.entities.ShippingInfoCustomerOrder;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ShippingInfoCustomerOrderService implements IModelService<ShippingInfoCustomerOrder, Long> {

    private ShippingInfoCustomerOrderDao shippingInfoCustomerOrderDao;

    public ShippingInfoCustomerOrderService(ShippingInfoCustomerOrderDao shippingInfoCustomerOrderDao)
    {

        this.shippingInfoCustomerOrderDao = shippingInfoCustomerOrderDao;
    }

    @Override
    public Optional<ShippingInfoCustomerOrder> save(ShippingInfoCustomerOrder shippingInfoCustomerOrder) {
        shippingInfoCustomerOrderDao.create(shippingInfoCustomerOrder);
        return Optional.of(shippingInfoCustomerOrder);
    }

    @Override
    public Optional<ShippingInfoCustomerOrder> findById(Long id) {
        return shippingInfoCustomerOrderDao.findById(id);
    }

    @Override
    public List<ShippingInfoCustomerOrder> findAll() {
        return shippingInfoCustomerOrderDao.findAll();
    }

    @Override
    public Optional<ShippingInfoCustomerOrder> update(ShippingInfoCustomerOrder shippingInfoCustomerOrder, Long id) {
        shippingInfoCustomerOrderDao.update(shippingInfoCustomerOrder, id);
        return Optional.of(shippingInfoCustomerOrder);
    }

    @Override
    public void delete(Long id) {
        shippingInfoCustomerOrderDao.delete(id);
    }

    public Optional<ShippingInfoCustomerOrder> findByOrderId(Long orderId) {
        return shippingInfoCustomerOrderDao.findByOrderId(orderId);
    }

    public Map<Long, String> idStatusMap() {
        return shippingInfoCustomerOrderDao.idStatusMap();
    }
    
}
