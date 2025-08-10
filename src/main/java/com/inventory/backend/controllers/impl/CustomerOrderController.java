package com.inventory.backend.controllers.impl;
import java.sql.Date;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.entities.OrderReturns;
import com.inventory.backend.entities.Product;
import com.inventory.backend.entities.ShippingInfoCustomerOrder;
import com.inventory.backend.services.impl.CustomerOrderService;
import com.inventory.backend.services.impl.CustomerService;
import com.inventory.backend.services.impl.OrderReturnsService;
import com.inventory.backend.services.impl.ProductService;
import com.inventory.backend.services.impl.ShippingInfoCustomerOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller // Change from @RestController to @Controller

public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final CustomerService customerService;

    @Autowired
    private ShippingInfoCustomerOrderService shippingInfoCustomerOrderService;

    @Autowired
    private OrderReturnsService orderReturnsService;

    @Autowired
    private ProductService productService;

    @Autowired
    public CustomerOrderController(CustomerOrderService customerOrderService,
                                   CustomerService customerService) {
        this.customerOrderService = customerOrderService;
        this.customerService = customerService;
    }

    // Endpoint to display all orders for a specific customer by email
    @GetMapping("/orders")
    public String getOrdersByCustomerEmail(Model model, Principal principal) {
        // Get the email of the currently logged in user
        String username = principal.getName();
        // Find the customer by username
        Optional<Customer> customerOptional = customerService.findByUsername(username);
        if (customerOptional.isEmpty()) {
            // Optionally add an attribute to display a message in the HTML
            model.addAttribute("message", "No customer found for this user.");
            return "customer-orders"; // Ensure this matches the name of your HTML file
        }
        String customerEmail = customerOptional.get().getEmailId();
        List<CustomerOrder> orders = customerOrderService.findOrdersByCustomerEmail(customerEmail);
        if (orders.isEmpty()) {
            // Optionally add an attribute to display a message in the HTML
            model.addAttribute("message", "It looks like you haven't placed any orders yet.");
        } else {
            model.addAttribute("orders", orders);
        }
        Map<Long, String> customerOrderStatusMap = shippingInfoCustomerOrderService.idStatusMap();
        model.addAttribute("customerOrderStatusMap", customerOrderStatusMap);
        // Return the name of the Thymeleaf template
        return "customer-orders"; // Ensure this matches the name of your HTML file
    }

    @GetMapping("/return-order/{orderId}")
    public String returnOrder(Model model, Principal principal, @PathVariable Long orderId) {
        // Implement the logic to return an order here
        String username = principal.getName();
        // Find the customer by username
        Optional<Customer> customerOptional = customerService.findByUsername(username);
        if (customerOptional.isEmpty()) {
            // Optionally add an attribute to display a message in the HTML
            model.addAttribute("message", "No customer found for this user.");
            return "customer-orders"; // Ensure this matches the name of your HTML file
        }

        ShippingInfoCustomerOrder shippingInfoCustomerOrder = shippingInfoCustomerOrderService.findByOrderId(orderId).get();
        shippingInfoCustomerOrder.setStatus(ShippingInfoCustomerOrder.Status.CANCELLED);

        shippingInfoCustomerOrderService.update(shippingInfoCustomerOrder, shippingInfoCustomerOrder.getShippingInfoId());

        CustomerOrder customerOrder = customerOrderService.findById(orderId).get();

       OrderReturns orderReturns = new OrderReturns();
                    orderReturns.setOrder(customerOrder);
                    orderReturns.setReturnDate(Date.valueOf(LocalDate.now()));
                    orderReturns.setReturnReason("Customer requested return");

                    orderReturnsService.save(orderReturns);

                    for (CustomerOrder.Pair<Product, Integer> product : customerOrder.getProducts()) {
                        productService.updateProductQuantity(product.getFirst().getProductId(), -product.getSecond());
                    }


        
        return "redirect:/orders"; // Redirect to the orders page
    }
}
