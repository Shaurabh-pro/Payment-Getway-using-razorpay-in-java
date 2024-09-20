package com.paymentGetway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymentGetway.model.Orders;

public interface OrderRepository extends JpaRepository<Orders, Integer> {

	Orders findByRazorpayOrderId(String razorpayId);

}
