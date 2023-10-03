package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Orders;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository  extends CrudRepository<Orders, Long> {
}