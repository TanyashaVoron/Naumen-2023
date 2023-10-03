package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Products;
import org.springframework.data.repository.CrudRepository;

public interface ProductsRepository  extends CrudRepository<Products, Long> {
}