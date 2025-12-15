package com.example.stage1monolithorder.repository;

import com.example.stage1monolithorder.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(Product product);
}

