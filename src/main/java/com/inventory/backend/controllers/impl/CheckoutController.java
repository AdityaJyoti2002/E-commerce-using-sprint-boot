package com.inventory.backend.controllers.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inventory.backend.entities.Cart;
import com.inventory.backend.services.impl.CartService;

import org.springframework.ui.Model;


@Controller
public class CheckoutController {


    @Autowired
    private CartService cartService;

    // Handler for the /checkout page
    @GetMapping("/checkout")
    public String checkoutPage(Model model, @RequestParam("totalAmount") double totalAmount,
                                            @RequestParam("cartId") Long cartId) {
        // Add the total amount to the model
        model.addAttribute("totalAmount", totalAmount);
        Optional<Cart> cartOptional = cartService.findById(cartId);

        Cart cart = cartOptional.orElse(null);

        model.addAttribute("cart", cart);

        model.addAttribute("customerEmail", cart.getCustomer().getEmailId());

        // Optionally, add any other necessary attributes, such as Razorpay key or order id
        model.addAttribute("razorpayKeyId", "your_razorpay_key_id_here");
        model.addAttribute("razorpayOrderId", "razorpay_order_id_here");

        // Return the view name (the Thymeleaf template to render)
        return "checkout";  // This should match the name of your checkout.html template
    }
}