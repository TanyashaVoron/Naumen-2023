package com.naumen.anticafe.serviceImpl.guest;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.service.guest.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    @Autowired
    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /**
     * Добавляет гостя в заказ
     */
    @Override
    @Transactional
    public void addGuest(Order order) {
        Guest guest = new Guest();
        //получает и инкриминирует номер макс гостя
        Optional<Guest> optionalGuest = guestRepository.findTopByCompositeIdOrderOrderByCompositeIdDesc(order);
        long count;
        if (optionalGuest.isEmpty()) count = 0;
        else count = optionalGuest.get().getCompositeId().getGuestId();
        count++;
        guest.setCompositeId(new GuestId(count, order));
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
    }

    /**
     * Удаляет гостя без товаров из заказа
     */
    @Override
    @Transactional
    public Integer deleteGuestByNotExistCart(GuestId guestId) {
        return guestRepository.deleteByCompositeId(guestId);
    }
}
