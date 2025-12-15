package com.example.stage1monolithorder.repository;

import com.example.stage1monolithorder.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);
}

