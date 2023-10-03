package com.naumen.anticafe.repository;


import com.naumen.anticafe.domain.GameZones;
import org.springframework.data.repository.CrudRepository;

public interface GameZonesRepository  extends CrudRepository<GameZones, Long> {
}