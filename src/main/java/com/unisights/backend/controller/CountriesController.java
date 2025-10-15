package com.unisights.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
@SecurityRequirement(name = "bearer-jwt")
public class CountriesController {
    private final JdbcTemplate j;
    public CountriesController(JdbcTemplate j){ this.j=j; }

    @GetMapping
    public List<String> list(){
        return j.query("select distinct country from universities where country is not null and trim(country)<>'' order by country",
                (rs,i)-> rs.getString(1));
    }
}
