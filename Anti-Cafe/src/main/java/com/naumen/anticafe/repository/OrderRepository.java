package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends CrudRepository<Order, Long> {

    List<Order> findByIdAndGameZoneOrderById(Long id, GameZone gameZone);
    List<Order> findAllByGameZoneOrderById(GameZone gameZone);
    List<Order> findAllById(Long id);
    List<Order> findAllByOrderById();
    List<Order> findAllByGameZoneAndReserveDateOrderByReserveDate(GameZone gameZone, LocalDate localDate);
    @Query("SELECT o FROM Order o WHERE (:id IS NULL OR o.id = :id) AND (:gameZone IS NULL OR o.gameZone = :gameZone) AND (:payment IS NULL OR o.payment = :payment) AND (:date IS NULL OR o.reserveDate =:date) AND (:employee IS NULL OR o.manager =:employee)")
    List<Order> findAllByIdAndGameZoneAndPaymentAndReserveDateAndManager(@Param("id") Long id,
                                                                         @Param("gameZone") GameZone gameZone,
                                                                         @Param("payment") Boolean payment,
                                                                         @Param("date") LocalDate reserveDate,
                                                                         @Param("employee") Employee manager
    );

}