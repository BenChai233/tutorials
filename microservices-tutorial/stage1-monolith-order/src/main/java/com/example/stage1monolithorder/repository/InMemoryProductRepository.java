package com.example.stage1monolithorder.repository;

import com.example.stage1monolithorder.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public InMemoryProductRepository() {
        save(new Product(null, "Java 入门书籍", new BigDecimal("79.00")));
        save(new Product(null, "机械键盘", new BigDecimal("399.00")));
        save(new Product(null, "降噪耳机", new BigDecimal("899.00")));
    }

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        storage.put(product.getId(), product);
        return product;
    }
}

