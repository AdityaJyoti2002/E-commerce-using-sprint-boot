package com.inventory.backend.controllers.impl;

import com.inventory.backend.entities.Customer;
import com.inventory.backend.services.impl.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        Optional<Customer> customerOptional = customerService.findByUsername(username);

        if (customerOptional.isPresent()) {
            model.addAttribute("customer", customerOptional.get());
            return "profile"; // Name of the Thymeleaf template for the profile page.
        } else {
            model.addAttribute("message", "Customer not found");
            return "notfound"; // A custom page indicating that the customer wasn't found.
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("customer") Customer customer, Model model) {
        customerService.update(customer, customer.getEmailId()); // Pass the ID explicitly.
        model.addAttribute("message", "Profile updated successfully");
        return "redirect:/profile"; // Redirects back to the profile page after update.
    }
}
