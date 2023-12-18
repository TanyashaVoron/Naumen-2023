package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Modifying
    @Query("UPDATE Order o " +
            "set o.taggedDelete = :taggedDelete," +
            " o.timerTaggedDelete = :localDate " +
            "WHERE o.id =:id")
    void setTaggedDeleteAndTimerTaggedDelete(Long id, Boolean taggedDelete, LocalDate localDate);

    List<Order> findAllByGameZoneAndReserveDateOrderByReserveDate(GameZone gameZone, LocalDate reserveDate);

    @Modifying
    @Query("UPDATE Order o " +
            "set o.gameZone = null," +
            " o.reserveDate = null," +
            " o.reserveTime = null," +
            " o.endReserve = null " +
            "where o.id=:id")
    void deleteReserve(Long id);

    @Query("SELECT o FROM Order o WHERE (:id IS NULL OR o.id = :id)" +
            " AND (:gameZoneId IS NULL OR o.gameZone.id =:gameZoneId)" +
            " AND (:payment IS NULL OR o.payment = :payment)" +
            " AND (:reserveDate IS NULL OR o.reserveDate =:reserveDate)" +
            " AND (:managerId IS NULL OR o.manager.id =:managerId)" +
            " AND (o.taggedDelete=:taggedDelete)")
    Page<Order> findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(Long id,
                                                                                        Long gameZoneId,
                                                                                        Boolean payment,
                                                                                        LocalDate reserveDate,
                                                                                        Long managerId,
                                                                                        Boolean taggedDelete,
                                                                                        Pageable pageable
    );

    List<Order> findAllByTimerTaggedDelete(LocalDate timeTaggedDelete);
}