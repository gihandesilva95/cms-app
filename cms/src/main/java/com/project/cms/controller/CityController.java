package com.project.cms.controller;

import com.project.cms.entity.City;
import com.project.cms.repository.CityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin
public class CityController {
    private final CityRepository repo;
    public CityController(CityRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<City> all() {
        return repo.findAll();
    }
}
