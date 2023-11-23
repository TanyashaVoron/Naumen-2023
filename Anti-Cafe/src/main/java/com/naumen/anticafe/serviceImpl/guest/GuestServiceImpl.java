package com.naumen.anticafe.serviceImpl.guest;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.service.guest.GuestService;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;
    private final GuestCartService guestCartService;
    @Autowired
    public GuestServiceImpl(GuestRepository guestRepository, GuestCartService guestCartService) {
        this.guestRepository = guestRepository;
        this.guestCartService = guestCartService;
    }

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

    @Override
    @Transactional
    public long deleteGuest(Guest guest){
        //проверяет есть ли у гостя товары
        long countProduct = guestCartService.countByGuest(guest);
        if(countProduct !=0)return guest.getCompositeId().getGuestId();
        guestRepository.delete(guest);
        return 0;
    }

    @Override
    @Transactional
    public void deleteGuestCascade(Guest guest) {
        //удаляет гостей вместе с их товарами
        guest.getCompositeId().getOrder().getGuests().remove(guest);
        guestRepository.deleteGuestByCompositeId(guest.getCompositeId());
    }
    @Override
    @Transactional(readOnly = true)
    public Guest getGuest(Long guestId, Order order) throws NotFoundException {
        Optional<Guest> optionalGuest = guestRepository.findById(new GuestId(guestId, order));
        if (optionalGuest.isEmpty()) throw new NotFoundException("Гость не найден");
        return optionalGuest.get();
    }
}
