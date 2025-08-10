// package com.inventory.backend.entities;

// import com.inventory.backend.embeddable.SalesReportCompositeKey;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class SalesReport {
    
//     private SalesReportCompositeKey salesReportCompositeKey;

//     private Double totalSales;

//     private Integer totalOrders;

//     private Product topSellingProduct;

// }
package com.inventory.backend.entities;

import com.inventory.backend.embeddable.SalesReportCompositeKey;

public class SalesReport {

    private SalesReportCompositeKey salesReportCompositeKey;
    private Double totalSales;
    private Integer totalOrders;
    private Product topSellingProduct;

    public SalesReport() {
    }

    public SalesReport(SalesReportCompositeKey salesReportCompositeKey, Double totalSales, Integer totalOrders, Product topSellingProduct) {
        this.salesReportCompositeKey = salesReportCompositeKey;
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.topSellingProduct = topSellingProduct;
    }

    // Getters and Setters

    public SalesReportCompositeKey getSalesReportCompositeKey() {
        return salesReportCompositeKey;
    }

    public void setSalesReportCompositeKey(SalesReportCompositeKey salesReportCompositeKey) {
        this.salesReportCompositeKey = salesReportCompositeKey;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Product getTopSellingProduct() {
        return topSellingProduct;
    }

    public void setTopSellingProduct(Product topSellingProduct) {
        this.topSellingProduct = topSellingProduct;
    }
}
