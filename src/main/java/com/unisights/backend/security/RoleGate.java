package com.unisights.backend.security;

import org.springframework.stereotype.Component;

@Component
public class RoleGate {
    // TODO replace with real auth; today we simulate
    public boolean isStudent(){ return true; }         // assume current browser user is student
    public boolean isConsultant(){ return true; }      // flip to false on real auth
}
