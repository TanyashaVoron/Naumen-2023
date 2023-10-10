package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {
}
