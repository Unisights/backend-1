package com.unisights.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    // @GetMapping("/auth/login")  public String login(){ return "auth/login"; }
    // @GetMapping("/auth/signup") public String signup(){ return "auth/signup"; }
    @GetMapping("/programs")    public String programs(){ return "programs"; }
}