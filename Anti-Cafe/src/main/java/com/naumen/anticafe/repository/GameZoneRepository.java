package com.naumen.anticafe.repository;


import com.naumen.anticafe.domain.GameZone;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameZoneRepository  extends CrudRepository<GameZone, Long> {
    List<GameZone> findAll();
}