package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.ProductCategories;
import org.springframework.data.repository.CrudRepository;

public interface ProductCategoriesRepository  extends CrudRepository<ProductCategories, Long> {
}