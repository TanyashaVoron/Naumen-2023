package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.error.NotFoundException;

import java.util.List;

public interface GameZoneService {
    List<GameZone> getGameZoneList();
    GameZone getGameZone(Long gameZoneId) throws NotFoundException;
}
