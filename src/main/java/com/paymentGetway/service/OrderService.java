package com.paymentGetway.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.paymentGetway.model.Orders;
import com.paymentGetway.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;

@Service
public class OrderService {

    @Autowired
    private OrderRepository ordersRepository;

    @Value("${razorpay_key_id}")
    private String razorpayId;

    @Value("${razorpay_key_secret}")
    private String razorpaySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayId, razorpaySecret); // Initialize Razorpay client with the API keys
    }

    // Create Razorpay order and save it to the database
    public Orders createOrder(Orders order) throws RazorpayException {
        // Create a Razorpay order
        JSONObject options = new JSONObject();
        options.put("amount", order.getAmount() * 100); // Amount in paise
        options.put("currency", "INR");
        options.put("receipt", order.getEmail());

        // Using the initialized RazorpayClient instance to create an order
        Order razorpayOrder = razorpayClient.orders.create(options); // Correct initialization

        if (razorpayOrder != null) {
            order.setRazorpayOrderId(razorpayOrder.get("id")); // Get the 'id' from the Razorpay order
            order.setOrderStatus(razorpayOrder.get("status")); // Get the 'status' from the Razorpay order
        }

        // Save the order to the database
        return ordersRepository.save(order);
    }

    // Update order status based on the callback response
    public Orders updateStatus(Map<String, String> map) {
        String razorpayOrderId = map.get("razorpay_order_id");
        Orders order = ordersRepository.findByRazorpayOrderId(razorpayOrderId);

        if (order != null) {
            order.setOrderStatus("PAYMENT DONE"); // Set order status to 'PAYMENT DONE'
            return ordersRepository.save(order);  // Save the updated order
        }

        return null; // Return null if order is not found (this should be handled appropriately)
    }
}
