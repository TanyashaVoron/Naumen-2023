package com.naumen.anticafe.serviceImpl.gamezone;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameZoneServiceImplTest {
    @InjectMocks
    private GameZoneServiceImpl gameZoneService;
    @Mock
    private GameZoneRepository gameZoneRepository;

    @SneakyThrows
    @Test
    void getGameZone_found_andException() {
        GameZone gameZone = new GameZone(1L,"name");
        Long gameZoneId = 1L;
        Long exceptionId = 2L;
        //найден
        Mockito.when(gameZoneRepository.findById(gameZoneId)).thenReturn(Optional.of(gameZone));
        Assertions.assertEquals(gameZoneService.getGameZone(gameZoneId), gameZone);
        Mockito.verify(gameZoneRepository, Mockito.times(1)).findById(gameZoneId);
        //ненайден
        Mockito.when(gameZoneRepository.findById(exceptionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> gameZoneService.getGameZone(exceptionId));
        Mockito.verify(gameZoneRepository, Mockito.times(1)).findById(exceptionId);
    }

    @Test
    void getGameZoneList() {
        GameZone gameZone = new GameZone(1L,"name");
        List<GameZone> gameZoneList1 = new ArrayList<>(List.of(gameZone));
        List<GameZone> gameZoneList2 = new ArrayList<>(List.of(gameZone));
        Mockito.when(gameZoneRepository.findAll()).thenReturn(gameZoneList1);
        Assertions.assertIterableEquals(gameZoneService.getGameZoneList(),gameZoneList2);
        Mockito.verify(gameZoneRepository,Mockito.times(1)).findAll();
    }
}