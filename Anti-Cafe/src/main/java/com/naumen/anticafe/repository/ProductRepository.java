package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}