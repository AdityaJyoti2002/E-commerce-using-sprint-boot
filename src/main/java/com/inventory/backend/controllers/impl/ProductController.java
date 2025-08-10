package com.inventory.backend.controllers.impl;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inventory.backend.entities.Cart;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.Product;
import com.inventory.backend.services.impl.CartService;
import com.inventory.backend.services.impl.CustomerService;
import com.inventory.backend.services.impl.ProductService;

import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;


    @GetMapping("/product/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOptional = productService.findById(id);
        System.out.println(productOptional);
        // If product is found, show details page
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            model.addAttribute("products", productService.getByCategory(productOptional.get().getCategory().getCategoryId(), 4));
            return "product-details"; // Thymeleaf template name (product-details.html)
        } else {
            // Redirect to notfound.html if product not found
            return "redirect:/notfound";
        }
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity, Model model, Principal principal) {
        // Add to cart logic here
        String username = principal.getName();
        // Find the customer by username
        Optional<Customer> customerOptional = customerService.findByUsername(username);
        if (customerOptional.isEmpty()) {
            model.addAttribute("message", "No customer found for this user.");
            return "notfound";
        }
        String customerEmail = customerOptional.get().getEmailId();
        Long cartId = cartService.getCartIdByCustomerEmail(customerEmail);

        Optional<Cart> cartOptional = cartService.findById(cartId);
        if (cartOptional.isEmpty()) {
            Cart cart = new Cart();
            cart.setCustomer(customerOptional.get());
            cartService.save(cart);
            cartId = cartService.getCartIdByCustomerEmail(customerEmail);
        }

        cartService.addProduct(cartId, productId, quantity);
        model.addAttribute("message", "Added to cart successfully.");
        model.addAttribute("quantity", quantity);
        Optional<Product> productOptional = productService.findById(productId);
        model.addAttribute("product", productOptional.get());
        model.addAttribute("products", productService.getByCategory(productOptional.get().getCategory().getCategoryId(), 4));
        return "product-details";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        model.addAttribute("products", productService.searchProducts(query));
        return "search";
    }
}
