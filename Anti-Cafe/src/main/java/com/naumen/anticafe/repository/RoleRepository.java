package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository  extends CrudRepository<Role, Long> {
    List<Role> findAll();
}