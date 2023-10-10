package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guest;
import org.springframework.data.repository.CrudRepository;

public interface GuestRepository  extends CrudRepository<Guest, Long> {
}
