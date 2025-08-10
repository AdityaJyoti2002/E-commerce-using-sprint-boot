package com.inventory.backend.controllers.impl;

import com.inventory.backend.dao.impl.ProductDao;
import com.inventory.backend.entities.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ClientInventory {

    private final ProductDao productDao;

    public ClientInventory(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping("/client")
    public String showClientInventory(Model model) {
        // Fetch the list of products from the database
        List<Product> products = productDao.findAll();
        // Add the products to the model
        model.addAttribute("products", products);
        // Return the view name
        return "clientInventory"; // Ensure this matches the Thymeleaf template name
    }
}
