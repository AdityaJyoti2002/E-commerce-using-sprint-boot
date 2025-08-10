package com.inventory.backend.controllers.impl;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inventory.backend.entities.Customer;
import com.inventory.backend.services.impl.CustomerService;


@Controller
public class LoginController {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private CustomerService customerService;
    private PasswordEncoder passwordEncoder;

    public LoginController(JdbcUserDetailsManager jdbcUserDetailsManager,
                            CustomerService customerService,
                            PasswordEncoder passwordEncoder) {
        this.jdbcUserDetailsManager = jdbcUserDetailsManager;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("customer", new Customer());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("customer") Customer customer,
                        @RequestParam("password") String password,
                        Model model) {
        String username = customer.getUsername();
        if (jdbcUserDetailsManager.userExists(username)) {
            model.addAttribute("message", "User already exists.");
            model.addAttribute("customer", customer);
            return "signup";
        }
        if(customerService.findById(customer.getEmailId()).isPresent()){
            model.addAttribute("message", "User already exists with this email.");
            model.addAttribute("customer", customer);
            return "signup";
        }
        UserDetails user = new User(
                    username,
                    passwordEncoder.encode(password),
                    AuthorityUtils.createAuthorityList("ROLE_USER")
                );


        jdbcUserDetailsManager.createUser(user);
        customerService.save(customer);
        model.addAttribute("message", "User created successfully.");
        return "redirect:/login";


    }
    

}
