package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository  extends CrudRepository<Order, Long> {
}