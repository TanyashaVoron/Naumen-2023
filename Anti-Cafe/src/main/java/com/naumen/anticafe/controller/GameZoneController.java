package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.repository.GameZoneRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/gameZone")
public class GameZoneController {
    private final GameZoneRepository gameZoneRepository;

    public GameZoneController(GameZoneRepository gameZoneRepository) {
        this.gameZoneRepository = gameZoneRepository;
    }

    @GetMapping
    public String getAllGameZone(Model model) {
        List<GameZone> gamezone = gameZoneRepository.findAll();
        model.addAttribute("gameZone", gamezone);
        return "gameZone";
    }

    @GetMapping("/add")
    public String showAddGameZone(Model model) {
        // Отобразить форму для добавления нового стола
        model.addAttribute("gameZone", new GameZone());
        return "gameZone_add";
    }

    @PostMapping("/add")
    public String addGameZone(@ModelAttribute GameZone gameZone) {
        gameZoneRepository.save(gameZone);
        return "redirect:/gameZone";
    }

    @GetMapping("/edit/{id}")
    public String showEditGameZone(@PathVariable Long id, Model model) {
        Optional<GameZone> optionalGameZone = gameZoneRepository.findById(id);
        if (optionalGameZone.isPresent()) {
            model.addAttribute("gameZone", optionalGameZone.get());
            return "gameZone_add";
        } else {
            return "redirect:/gameZone";
        }
    }

    @PostMapping("/edit")
    public String editGameZone(@ModelAttribute GameZone gameZone) {
        gameZoneRepository.save(gameZone);
        return "redirect:/gameZone";
    }

    @GetMapping("/delete/{id}")
    public String deleteGameZone(@PathVariable Long id) {
        gameZoneRepository.deleteById(id);
        return "redirect:/gameZone";
    }
}

