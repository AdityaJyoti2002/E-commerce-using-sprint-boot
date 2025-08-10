package com.inventory.backend.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Product {

    private Long productId;
    private String productName;
    private String description;
    private Date expiryDate;
    private Integer stockQuantity;
    private Double sellingPrice;
    private Double maximumRetailPrice;
    private Category category;
    private String imageUrl;
    private Set<Pair<Manufacturer, Double>> manufacturers = new HashSet<>();

    // No-args constructor
    public Product() {}

    // All-args constructor
    public Product(Long productId, String productName, String description, Date expiryDate, Integer stockQuantity,
                   Double sellingPrice, Double maximumRetailPrice, Category category, String imageUrl,
                   Set<Pair<Manufacturer, Double>> manufacturers) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.expiryDate = expiryDate;
        this.stockQuantity = stockQuantity;
        this.sellingPrice = sellingPrice;
        this.maximumRetailPrice = maximumRetailPrice;
        this.category = category;
        this.imageUrl = imageUrl;
        this.manufacturers = manufacturers;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getMaximumRetailPrice() {
        return maximumRetailPrice;
    }

    public void setMaximumRetailPrice(Double maximumRetailPrice) {
        this.maximumRetailPrice = maximumRetailPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Pair<Manufacturer, Double>> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(Set<Pair<Manufacturer, Double>> manufacturers) {
        this.manufacturers = manufacturers;
    }

    // Inner Pair class
    public static class Pair<T, U> {
        private T first;
        private U second;
    
        // Constructor
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    
        // Getter for first
        public T getFirst() {
            return first;
        }
    
        // Setter for first
        public void setFirst(T first) {
            this.first = first;
        }
    
        // Getter for second
        public U getSecond() {
            return second;
        }
    
        // Setter for second
        public void setSecond(U second) {
            this.second = second;
        }
    }
    
}
