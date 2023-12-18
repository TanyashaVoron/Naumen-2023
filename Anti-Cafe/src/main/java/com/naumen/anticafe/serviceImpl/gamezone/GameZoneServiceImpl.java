package com.naumen.anticafe.serviceImpl.gamezone;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameZoneServiceImpl implements GameZoneService {
    private final GameZoneRepository gameZoneRepository;

    @Autowired
    public GameZoneServiceImpl(GameZoneRepository gameZoneRepository) {
        this.gameZoneRepository = gameZoneRepository;
    }

    /**
     * Передает игровую зону по ИД
     */
    @Transactional(readOnly = true)
    public GameZone getGameZone(Long gameZoneId) throws NotFoundException {
        //если не находит выбрасывает ошибку
        Optional<GameZone> optionalOrder = gameZoneRepository.findById(gameZoneId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Игровая зона не найдена");
        return optionalOrder.get();
    }

    /**
     * Передает игровые зоны
     */
    @Override
    @Transactional(readOnly = true)
    public List<GameZone> getGameZoneList() {
        return (List<GameZone>) gameZoneRepository.findAll();
    }
}
