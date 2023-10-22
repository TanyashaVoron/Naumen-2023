package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.ProductCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductCategoryRepository  extends CrudRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByName(String name);
}