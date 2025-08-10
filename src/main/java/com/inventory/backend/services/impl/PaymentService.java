package com.inventory.backend.services.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    public String createOrder(double amount) throws Exception {
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Order parameters
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);  // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_receipt_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);
        return order.get("id").toString();
    }
}
