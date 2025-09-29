package com.unisights.backend.security;

import org.springframework.stereotype.Component;

@Component
public class RoleGate {

    public boolean isStudent() {
        return true;
    }

    public boolean isConsultant() {
        return true;
    }
}

