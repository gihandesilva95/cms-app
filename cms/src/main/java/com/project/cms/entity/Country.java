package com.project.cms.entity;

import javax.persistence.*;

@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}