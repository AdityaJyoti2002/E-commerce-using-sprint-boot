package com.inventory.backend.controllers.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inventory.backend.entities.Category;
import com.inventory.backend.entities.Product;
import com.inventory.backend.services.impl.CategoryServiceImpl;
import com.inventory.backend.services.impl.ProductService;

@Controller
public class HomePageController {

    private final CategoryServiceImpl categoryService;
    private final ProductService productService;

    public HomePageController(CategoryServiceImpl categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> allCategories = categoryService.findAll();
        List<Product> deals = productService.findDealsOfTheDay();

        model.addAttribute("categories", allCategories);
        model.addAttribute("dealGroups", partitionList(deals, 5)); // partitioned list

        return "index";
    }

    private List<List<Product>> partitionList(List<Product> deals, int size) {
        List<List<Product>> partitions = new ArrayList<>();
        for (int i = 0; i < deals.size(); i += size) {
            partitions.add(deals.subList(i, Math.min(i + size, deals.size())));
        }
        return partitions;
    }
}
