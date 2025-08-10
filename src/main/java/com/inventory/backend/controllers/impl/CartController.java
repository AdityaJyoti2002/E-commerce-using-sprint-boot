package com.inventory.backend.controllers.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inventory.backend.entities.Cart;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.services.impl.CartService;
import com.inventory.backend.services.impl.CustomerService;

@Controller
public class CartController {
    
    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;


   @GetMapping("/cart")
public String showCart(Principal principal, Model model) {
    String username = principal.getName();
    Optional<Customer> customerOptional = customerService.findByUsername(username);
    if (customerOptional.isEmpty()) {
        model.addAttribute("message", "No customer found for this user.");
        return "notfound";
    }

    String customerEmail = customerOptional.get().getEmailId();
    Long cartId = cartService.getCartIdByCustomerEmail(customerEmail);
    Optional<Cart> cartOptional = cartService.findById(cartId);

    Cart cart;
    if (cartOptional.isEmpty()) {
      
        cart = new Cart();
cart.setCustomer(customerOptional.get());
cart.setProducts(new HashSet<>());
        cartService.save(cart);
    } else {
        cart = cartOptional.get();
       
        if (cart.getProducts() == null) {
            cart.setProducts(new HashSet<>());
        }
    }

    model.addAttribute("cart", cart);
    return "cart";
}

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("productId") Long productId, 
                                     @RequestParam("quantity") int quantity,
                                     @RequestParam("cartId") Long cartId) {
        cartService.addProduct(cartId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("productId") Long productId, 
                                     @RequestParam("cartId") Long cartId) {
        cartService.removeProduct(cartId, productId);
        return "redirect:/cart";
    }

}
