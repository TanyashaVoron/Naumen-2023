package com.naumen.anticafe.serviceImpl.guestCart;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GuestCartRepository;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuestCartServiceImpl implements GuestCartService {

    private final GuestCartRepository guestCartRepository;

    @Autowired
    public GuestCartServiceImpl(GuestCartRepository guestCartRepository) {
        this.guestCartRepository = guestCartRepository;
    }

    /**
     * передать всю корзину заказа
     */
    @Override
    @Transactional(readOnly = true)
    public List<GuestCart> getGuestCartListByOrder(Order order) {
        List<Guest> guests = order.getGuests();
        List<GuestCart> guestCartList = new ArrayList<>();
        for (Guest g : guests) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        return guestCartList;
    }
}
