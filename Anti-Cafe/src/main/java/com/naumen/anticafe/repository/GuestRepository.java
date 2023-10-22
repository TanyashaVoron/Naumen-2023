package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestRepository  extends CrudRepository<Guest, Long> {
    List<Guest> findAllByOrder(Order order);
    long countByOrder(Order order);
}
