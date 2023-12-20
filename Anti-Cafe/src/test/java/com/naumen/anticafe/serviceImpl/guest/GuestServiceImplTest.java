package com.naumen.anticafe.serviceImpl.guest;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.service.guest.GuestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {
    @InjectMocks
    private GuestServiceImpl guestService;
    @Mock
    private GuestRepository guestRepository;

    @Test
    void addGuest_guest1() {
        Order order = new Order();
        Guest guest = new Guest();
        guest.setCompositeId(new GuestId(1L, order));
        order.setId(1L);
        Mockito.when(guestRepository.findTopByCompositeIdOrderOrderByCompositeIdDesc(order)).thenReturn(Optional.of(guest));
        guestService.addGuest(order);
        Mockito.verify(guestRepository).save(Mockito.argThat(guest1 -> {
            // Проверка на true перед установкой
            assertEquals(2L,guest1.getCompositeId().getGuestId());
            assertEquals(order,guest1.getCompositeId().getOrder());
            assertEquals("Гость №" + 2L,guest1.getName());
            return true;
        }));
    }
    @Test
    void addGuest_guest0() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(guestRepository.findTopByCompositeIdOrderOrderByCompositeIdDesc(order)).thenReturn(Optional.empty());
        guestService.addGuest(order);
        Mockito.verify(guestRepository).save(Mockito.argThat(guest1 -> {
            // Проверка на true перед установкой
            assertEquals(1L,guest1.getCompositeId().getGuestId());
            assertEquals(order,guest1.getCompositeId().getOrder());
            assertEquals("Гость №" + 1L,guest1.getName());
            return true;
        }));
    }

    @Test
    void deleteGuestByNotExistCart() {
        //нашел что удалить
        Order order = new Order();
        GuestId guestId = new GuestId(1L,order);
        Mockito.when(guestRepository.deleteByCompositeId(guestId)).thenReturn(1);
        assertEquals(1, guestService.deleteGuestByNotExistCart(guestId));
        //false
        guestId = new GuestId(2L,order);
        Mockito.when(guestRepository.deleteByCompositeId(guestId)).thenReturn(0);
        assertEquals(0, guestService.deleteGuestByNotExistCart(guestId));
    }
}