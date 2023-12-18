package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.ProductCategory;
import org.springframework.data.repository.CrudRepository;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, Long> {
}