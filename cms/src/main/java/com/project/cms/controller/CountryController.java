package com.project.cms.controller;

import com.project.cms.entity.Country;
import com.project.cms.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin
public class CountryController {
    private final CountryRepository repo;
    public CountryController(CountryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Country> all() {
        return repo.findAll();
    }
}
