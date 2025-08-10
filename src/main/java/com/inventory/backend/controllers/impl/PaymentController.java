package com.inventory.backend.controllers.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inventory.backend.entities.Cart;
import com.inventory.backend.entities.Customer;
import com.inventory.backend.entities.CustomerOrder;
import com.inventory.backend.services.impl.CustomerService;
import com.inventory.backend.services.impl.EmployeeService;
import org.apache.commons.lang3.tuple.Triple;
import com.inventory.backend.entities.Employee;
import com.inventory.backend.entities.Product;
import com.inventory.backend.services.impl.CartService;
import com.inventory.backend.services.impl.CustomerOrderService;
import com.inventory.backend.services.impl.PaymentService;
import com.inventory.backend.services.impl.ProductService;


@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Value("${razorpay.key_id}")
    private String razorpayKeyId;
    
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerOrderService customerOrderService;
    
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @PostMapping("/razorpay/payment")
    public String handlePayment(@RequestParam("totalAmount") double totalAmount, Model model,
                                @RequestParam("products") List<Long> productIds,
                                @RequestParam("quantities") List<Integer> quantities,
                                @RequestParam("cartId") Long cartId,
                                @RequestParam("paymentMethod") String paymentMethod) {

        Cart cart = cartService.findById(cartId).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        Customer customer = cart.getCustomer();

        if (paymentMethod.equals("CASH"))
        {
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setCustomer(customer);
            customerOrder.setDateOfOrder(Date.valueOf(LocalDate.now()));
            
            Employee employee = new Employee();
            employee.setEmployeeId(1L);
            customerOrder.setProcessorEmployee(employee);
            
            customerOrder.setPaymentMethod(CustomerOrder.PaymentMethod.CASH);
            for (int i = 0; i < productIds.size(); i++) {
                Product product = productService.findById(productIds.get(i)).get();
                customerOrder.getProducts().add(new CustomerOrder.Pair<>(product, quantities.get(i)));
            }

            customerOrderService.save(customerOrder);

            cartService.delete(cartId);

            return "redirect:/orders";
        }

        // Process the payment and create the Razorpay order
        try {
            // Create Razorpay order and return Razorpay order ID
            String razorpayOrderId = paymentService.createOrder(totalAmount);
            model.addAttribute("razorpayOrderId", razorpayOrderId);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("razorpayKeyId", razorpayKeyId); // Pass Razorpay key to frontend
            model.addAttribute("paymentMethod", paymentMethod);


            // Redirect to payment page for Razorpay checkout
            return "payment"; // Return the payment page to handle Razorpay payment flow
        } catch (Exception e) {
            model.addAttribute("error", "Payment processing failed: " + e.getMessage());
            return "error"; // Show error page if payment processing fails
        }
    }

    // Saving the order after successful payment
    @PostMapping("/razorpay/saveOrder")
    @ResponseBody
    public Map<String, Object> saveOrder(
            @RequestBody Map<String, Object> orderData,
            Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract necessary details from the orderData
            String paymentId = (String) orderData.get("razorpayPaymentId");
            String orderId = (String) orderData.get("razorpayOrderId");
            double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());
            String paymentMethod = (String) orderData.get("paymentMethod");

            // Retrieve customer based on the logged-in user (principal)
            Customer customer = customerService.findByUsername(principal.getName())
                    .orElseThrow(() -> new Exception("Customer not found"));

            // Retrieve the customer's cart
            Cart cart = cartService.getCartByCustomerEmail(customer); // Assuming getCartByCustomerEmail method

            // Create a CustomerOrder instance and populate it with the necessary details
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setCustomer(customer);
            customerOrder.setDateOfOrder(new java.sql.Date(System.currentTimeMillis()));
            if (paymentMethod.equals("CARD")) {
                customerOrder.setPaymentMethod(CustomerOrder.PaymentMethod.CARD);
            } 
            else if (paymentMethod.equals("NET BANKING")) {
                customerOrder.setPaymentMethod(CustomerOrder.PaymentMethod.NET_BANKING);
            }
            else {
                customerOrder.setPaymentMethod(CustomerOrder.PaymentMethod.CASH);
            }
            customerOrder.setProducts(convertCartProductsToOrderProducts(cart.getProducts())); // Convert cart products to order products
            Employee processorEmployee = employeeService.findById(1L).orElse(null);
            customerOrder.setProcessorEmployee(processorEmployee); // Set the processor employee
            // Save the order in the database
            customerOrderService.save(customerOrder);

            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

// Helper function to convert cart products to order products
private Set<CustomerOrder.Pair<Product, Integer>> convertCartProductsToOrderProducts(Set<Triple<Product, Integer, java.sql.Date>> cartProducts) {
    Set<CustomerOrder.Pair<Product, Integer>> orderProducts = new HashSet<>();

    // Loop through each item in the cart products
    for (Triple<Product, Integer, java.sql.Date> cartProduct : cartProducts) {
        // Extract the product, quantity and added date (you can discard the date if not needed)
        Product product = cartProduct.getLeft();
        Integer quantity = cartProduct.getMiddle();

        // Create a Pair and add it to the order products set
        CustomerOrder.Pair<Product, Integer> orderProduct = new CustomerOrder.Pair<>(product, quantity);
        orderProducts.add(orderProduct);
    }

    return orderProducts;
}

}
