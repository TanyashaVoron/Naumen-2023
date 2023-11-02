package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.service.GameZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameZoneServiceImpl implements GameZoneService {
    private final GameZoneRepository gameZoneRepository;

    @Autowired
    public GameZoneServiceImpl(GameZoneRepository gameZoneRepository) {
        this.gameZoneRepository = gameZoneRepository;
    }

    public GameZone getGameZone(Long gameZoneId) throws NotFoundException {
        Optional<GameZone> optionalOrder = gameZoneRepository.findById(gameZoneId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Игровая зона не найдена");
        return optionalOrder.get();
    }

    @Override
    public List<GameZone> getGameZoneList() {
        return gameZoneRepository.findAll();
    }
}
