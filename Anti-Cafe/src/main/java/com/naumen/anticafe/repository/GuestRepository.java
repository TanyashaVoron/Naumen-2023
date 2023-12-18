package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GuestRepository extends CrudRepository<Guest, GuestId> {
    @Modifying
    @Query("delete from Guest g" +
            " where g.compositeId=:guestId " +
            "and NOT EXISTS (SELECT 1 FROM GuestCart gc WHERE gc.guest = g)")
    Integer deleteByCompositeId(GuestId guestId);

    Optional<Guest> findTopByCompositeIdOrderOrderByCompositeIdDesc(Order order);

}
