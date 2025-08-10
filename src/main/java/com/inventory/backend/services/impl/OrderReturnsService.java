package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.OrderReturnsDao;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.OrderReturns;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class OrderReturnsService implements IModelService<OrderReturns, CustomerOrder> {

    private OrderReturnsDao orderReturnsDao;

    public OrderReturnsService(OrderReturnsDao orderReturnsDao) {
        this.orderReturnsDao = orderReturnsDao;
    }

    @Override
    public Optional<OrderReturns> save(OrderReturns orderReturns) {
        orderReturnsDao.create(orderReturns);
        return Optional.of(orderReturns);
    }

    @Override
    public Optional<OrderReturns> findById(CustomerOrder id) {
        return orderReturnsDao.findById(id);
    }

    @Override
    public List<OrderReturns> findAll() {
        return orderReturnsDao.findAll();
    }

    @Override
    public Optional<OrderReturns> update(OrderReturns orderReturns, CustomerOrder id) {
        orderReturnsDao.update(orderReturns, id);
        return Optional.of(orderReturns);
    }

    @Override
    public void delete(CustomerOrder id) {
        orderReturnsDao.delete(id);
    }

    public List<Long> getReturnedOrderIds() {
        return orderReturnsDao.findOrderIds();
    }

    
}
