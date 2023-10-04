package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.GuestCart;
import org.springframework.data.repository.CrudRepository;

public interface GuestCartRepository extends CrudRepository<GuestCart, Long> {
}