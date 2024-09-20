package com.paymentGetway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paymentGetway.model.Orders;
import com.paymentGetway.service.OrderService;
import com.razorpay.RazorpayException;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;  // Inject the service

    @GetMapping("/orders")
    public String orderPage() {
        return "orders";
    }

    @PostMapping(value = "/createOrder", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Orders> createOrder(@RequestBody Orders orders) {
        Orders razorpayOrder = null;
        try {
            razorpayOrder = orderService.createOrder(orders);  // Use the injected service
        } catch (RazorpayException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Handle error properly
        }
        return new ResponseEntity<>(razorpayOrder, HttpStatus.CREATED);  // Return the created order
    }

    @PostMapping("/paymentCallback")
    public String paymentCallback(@RequestParam Map<String, String> response) {
        orderService.updateStatus(response);  // Use the injected service
        return "success";
    }
}
