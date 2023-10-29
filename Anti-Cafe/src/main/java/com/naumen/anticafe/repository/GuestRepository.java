package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestRepository  extends CrudRepository<Guest, GuestId> {
    List<Guest> findAllByCompositeIdOrder(Order order);
    long countByCompositeIdOrder(Order order);
}
