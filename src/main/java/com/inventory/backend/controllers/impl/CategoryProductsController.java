package com.inventory.backend.controllers.impl;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.inventory.backend.entities.Product;
import com.inventory.backend.services.impl.ProductService;

@Controller
public class CategoryProductsController {
    
    private ProductService productService;

    public CategoryProductsController(ProductService productService) {
        this.productService = productService;
    }

    // @GetMapping("/category/{categoryId}/products")
    // public String getProductsByCategory(@PathVariable Long categoryId, Model model) {

    //     List<Product> products = productService.getByCategory(categoryId);

    //     if(products.isEmpty()) {
    //         return "notfound";
    //     }

    //     model.addAttribute("products", products);
    //     model.addAttribute("category", products.get(0).getCategory());

    //     return "category-products";
    // }
    @GetMapping("/category/{categoryId}/products")
    public String getProductsByCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = productService.getByCategory(categoryId);
    
        if (products.isEmpty()) {
            return "notfound";
        }
    
        var category = products.get(0).getCategory();
        if (category == null) {
            category = productService.getCategoryById(categoryId);
        }
    
        model.addAttribute("products", products);
        model.addAttribute("category", category);
    
        return "category-products";
    }
    
    

}
