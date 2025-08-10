package com.inventory.backend.services.impl;

import com.inventory.backend.dao.impl.SalesReportDao;
import com.inventory.backend.embeddable.SalesReportCompositeKey;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.Product;
import com.inventory.backend.entities.SalesReport;
import com.inventory.backend.services.IModelService;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

@Service
public class SalesReportService implements IModelService<SalesReport, SalesReportCompositeKey> {

    private SalesReportDao salesReportDao;

    public SalesReportService(SalesReportDao salesReportDao) {
        this.salesReportDao = salesReportDao;
    }

    @Override
    public Optional<SalesReport> save(SalesReport salesReport) {
        salesReportDao.create(salesReport);
        return Optional.of(salesReport);
    }

    @Override
    public Optional<SalesReport> findById(SalesReportCompositeKey id) {
        return salesReportDao.findById(id);
    }

    @Override
    public List<SalesReport> findAll() {
        return salesReportDao.findAll();
    }

    @Override
    public Optional<SalesReport> update(SalesReport salesReport, SalesReportCompositeKey id) {
        salesReportDao.update(salesReport, id);
        return Optional.of(salesReport);
    }

    public Optional<SalesReport> updateSalesReport(Date orderDate, CustomerOrder customerOrder)
    {
        SalesReportCompositeKey key = convertToKey(orderDate);
        Optional<SalesReport> salesReport = salesReportDao.findById(key);
        if (salesReport.isPresent()) {
            SalesReport sr = salesReport.get();
            for (CustomerOrder.Pair<Product, Integer> iterable_element : customerOrder.getProducts()) {
                sr.setTotalSales(sr.getTotalSales() + iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            sr.setTotalOrders(sr.getTotalOrders() + 1);
            salesReportDao.update(sr, key);
        } else {
            SalesReport newSalesReport = new SalesReport();
newSalesReport.setSalesReportCompositeKey(key);
newSalesReport.setTotalOrders(1);
newSalesReport.setTotalSales(0.0);
            for (CustomerOrder.Pair<Product, Integer> iterable_element : customerOrder.getProducts()) {
                newSalesReport.setTotalSales(newSalesReport.getTotalSales() + iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            salesReportDao.create(newSalesReport);
        }
        return salesReport;
    }


    public Optional<SalesReport> updateSalesReport(Date orderDate, CustomerOrder newCustomerOrder, CustomerOrder oldCustomerOrder)
    {
        SalesReportCompositeKey key = convertToKey(orderDate);
        Optional<SalesReport> salesReport = salesReportDao.findById(key);
        if (salesReport.isPresent()) {
            SalesReport sr = salesReport.get();
            for (CustomerOrder.Pair<Product, Integer> iterable_element : oldCustomerOrder.getProducts()) {
                sr.setTotalSales(sr.getTotalSales() - iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            for (CustomerOrder.Pair<Product, Integer> iterable_element : newCustomerOrder.getProducts()) {
                sr.setTotalSales(sr.getTotalSales() + iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            salesReportDao.update(sr, key);
        } else {
            SalesReport newSalesReport = new SalesReport();
            newSalesReport.setSalesReportCompositeKey(key);
            newSalesReport.setTotalOrders(1);
            newSalesReport.setTotalSales(0.0);
            // newSalesReport.setTopSellingProduct(null); // if needed
            
            for (CustomerOrder.Pair<Product, Integer> iterable_element : newCustomerOrder.getProducts()) {
                newSalesReport.setTotalSales(newSalesReport.getTotalSales() + iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            salesReportDao.create(newSalesReport);
        }
        return salesReport;
    }

    public Optional<SalesReport> removeCustomerOrderData(Date orderDate, CustomerOrder customerOrder)
    {
        SalesReportCompositeKey key = convertToKey(orderDate);
        Optional<SalesReport> salesReport = salesReportDao.findById(key);
        if (salesReport.isPresent()) {
            SalesReport sr = salesReport.get();
            for (CustomerOrder.Pair<Product, Integer> iterable_element : customerOrder.getProducts()) {
                sr.setTotalSales(sr.getTotalSales() - iterable_element.getFirst().getMaximumRetailPrice() * iterable_element.getSecond());
            }
            sr.setTotalOrders(sr.getTotalOrders() - 1);
            salesReportDao.update(sr, key);
        }
        return salesReport;
    }

    @Override
    public void delete(SalesReportCompositeKey id) {
        salesReportDao.delete(id);
    }   

    public TreeMap<Date, Double> weeklySales(Date startDate, Date endDate) {
        new SalesReportCompositeKey();


        List<SalesReport> sr = salesReportDao.weeklySales(startDate, endDate);
        
        Map <Date, Double> weeklySales = new java.util.HashMap<>();
        for (SalesReport salesReport : sr) {
            Date date = convertToDate(salesReport.getSalesReportCompositeKey());
            weeklySales.put(date, salesReport.getTotalSales());
        }
        // fill in missing dates with null
        Date currentDate = startDate;
        while (currentDate.before(endDate)) {
            if (!weeklySales.containsKey(currentDate)) {
                weeklySales.put(currentDate, 0.0);
            }
            currentDate = Date.valueOf(currentDate.toLocalDate().plusDays(1));
        }

        return new TreeMap<>(weeklySales);
    }

    private Date convertToDate(SalesReportCompositeKey key) {
        return Date.valueOf(key.getYear() + "-" + key.getMonth() + "-" + key.getDay());
    }

    private SalesReportCompositeKey convertToKey(Date date) {
        String[] dateParts = date.toString().split("-");
                    int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);

            return new SalesReportCompositeKey(day, month, year);
    }
    
}
