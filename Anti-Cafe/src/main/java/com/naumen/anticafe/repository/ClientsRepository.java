package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Clients;
import org.springframework.data.repository.CrudRepository;

public interface ClientsRepository extends CrudRepository<Clients, Long> {
}
