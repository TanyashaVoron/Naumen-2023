package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends CrudRepository<Order, Long> {

    List<Order> findByIdAndGameZoneOrderById(Long id, GameZone gameZone);
    List<Order> findAllByGameZoneOrderById(GameZone gameZone);
    List<Order> findAllById(Long id);
    List<Order> findAllByOrderById();
    List<Order> findAllByGameZoneAndReserveDateOrderByReserveDate(GameZone gameZone, LocalDate localDate);
}