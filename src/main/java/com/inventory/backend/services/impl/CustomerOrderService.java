package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.CustomerOrderDao;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.Product;
import com.inventory.backend.services.IModelService;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CustomerOrderService implements IModelService<CustomerOrder, Long> {

    private CustomerOrderDao customerOrderDao;
    private SalesReportService salesReportService;
    private ProductService productService;

    public CustomerOrderService(CustomerOrderDao customerOrderDao,
                                 SalesReportService salesReportService,
                                 ProductService productService) {
        this.customerOrderDao = customerOrderDao;
        this.salesReportService = salesReportService;
        this.productService = productService;
    }

    @Override
    public Optional<CustomerOrder> save(CustomerOrder customerOrder) {
        customerOrderDao.create(customerOrder);
        // update SalesReport
        Date orderDate = customerOrder.getDateOfOrder();
        salesReportService.updateSalesReport(orderDate, customerOrder);
        // update Products
        for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
            productService.updateProductQuantity(product.getFirst().getProductId(), product.getSecond());
        }

        return Optional.of(customerOrder);
    }

    @Override
    public Optional<CustomerOrder> findById(Long id) {
        return customerOrderDao.findById(id);
    }

    @Override
    public List<CustomerOrder> findAll() {
        return customerOrderDao.findAll();
    }

    @Override
    public Optional<CustomerOrder> update(CustomerOrder customerOrder, Long id) {
        CustomerOrder oldCustomerOrder = customerOrderDao.findById(id).get();
        customerOrderDao.update(customerOrder, id);
        salesReportService.updateSalesReport(customerOrder.getDateOfOrder(), customerOrder, oldCustomerOrder);
        return Optional.of(customerOrder);
    }

    @Override
    public void delete(Long id) {
        CustomerOrder customerOrder = customerOrderDao.findById(id).get();
        for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
            productService.updateProductQuantity(product.getFirst().getProductId(), -product.getSecond());
        }
        salesReportService.removeCustomerOrderData(customerOrder.getDateOfOrder(), customerOrder);
        customerOrderDao.delete(id);
    }

    public Map<String, Double> totalSalesByCategory() {
        return customerOrderDao.totalSalesByCategory();
    }

    public List<CustomerOrder> findOrdersByCustomerEmail(String customerEmail) {
        return customerOrderDao.findOrdersByCustomerEmail(customerEmail);
    }
    
    
}
