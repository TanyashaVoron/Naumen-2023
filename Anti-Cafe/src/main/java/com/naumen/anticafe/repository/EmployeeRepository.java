package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE Employee t set t.enabled = :enabled where t.id = :id")
    void setEnable(Long id, boolean enabled);

    Optional<Employee> findByUsername(String username);

    List<Employee> findAllByEnabled(boolean enabled);

    Page<Employee> findByUsernameContainsOrderByEnabledDesc(String username, Pageable pageable);
}