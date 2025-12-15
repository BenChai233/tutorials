package com.example.stage1monolithorder.service;

import com.example.stage1monolithorder.model.Order;
import com.example.stage1monolithorder.model.OrderItem;
import com.example.stage1monolithorder.model.Product;
import com.example.stage1monolithorder.repository.OrderRepository;
import com.example.stage1monolithorder.repository.ProductRepository;
import com.example.stage1monolithorder.web.dto.CreateOrderRequest;
import com.example.stage1monolithorder.web.dto.OrderItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    public Order createOrder(CreateOrderRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));
            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for product " + product.getId());
            }
            items.add(new OrderItem(product.getId(), itemRequest.getQuantity()));
        }

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setItems(items);
        return orderRepository.save(order);
    }
}

