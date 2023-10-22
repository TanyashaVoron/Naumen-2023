package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestCartRepository extends CrudRepository<GuestCart, Long> {
    List<GuestCart> findAllByGuest(Guest guest);
}