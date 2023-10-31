package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.GuestCartRepository;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.service.GuestService;
import com.naumen.anticafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class GuestServiceImpl implements GuestService {


    private final GuestCartRepository guestCartRepository;
    private final GuestRepository guestRepository;
    @Autowired
    public GuestServiceImpl(GuestCartRepository guestCartRepository,
                            GuestRepository guestRepository) {
        this.guestCartRepository = guestCartRepository;
        this.guestRepository = guestRepository;
    }
    @Override
    public List<GuestCart> getGuestCartListByGuest(List<Guest> guestList){
        List<GuestCart> guestCartList = new ArrayList<>();
        for (Guest g : guestList) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        return guestCartList;
    }
    @Override
    public List<Guest> getGuestListByOrder(Order order){
        return guestRepository.findAllByCompositeIdOrder(order);
    }
    @Override
    public void deleteGuest(Guest guest)throws GuestsHaveGoodsException {
        //проверяет есть ли у гостя товары
        checkProductGuest(guest);
        guestRepository.delete(guest);
    }
    private void checkProductGuest(Guest guest) throws GuestsHaveGoodsException {
        long countGuestCart = guestCartRepository.countByGuest(guest);
        if(countGuestCart!=0) throw new GuestsHaveGoodsException("У гостя есть товары",guest.getCompositeId().getGuestId());
    }
    public Guest getGuest(Long guestId, Order order) throws NotFoundException {
        Optional<Guest> optionalGuest = guestRepository.findById(new GuestId(guestId, order));
        if(optionalGuest.isEmpty()) throw new NotFoundException("Гость не найден");
        return optionalGuest.get();
    }
    @Override
    public void addGuest(Order order) throws NotFoundException {
        Guest guest = new Guest();
        //получает и инкриминирует номер макс гостя
        long count;
        Optional<Guest> optionalGuest = guestRepository.findTopByCompositeIdOrderOrderByCompositeIdDesc(order);
        if(optionalGuest.isEmpty()) count=0;
        else count = optionalGuest.get().getCompositeId().getGuestId();
        count++;
        guest.setCompositeId(new GuestId(count, order));
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
    }
    public List<GuestCart> getProductGuest(Guest guest){
        return guestCartRepository.findAllByGuest(guest);
    }
}
