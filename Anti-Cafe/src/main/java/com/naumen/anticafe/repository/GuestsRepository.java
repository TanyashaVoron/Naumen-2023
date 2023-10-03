package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guests;
import org.springframework.data.repository.CrudRepository;

public interface GuestsRepository  extends CrudRepository<Guests, Long> {
}
