package com.unisights.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AdminGuard {
    private final JwtService jwt;
    private final JwtService jwtService;

    public AdminGuard(JwtService jwt, JwtService jwtService){ this.jwt = jwt;
        this.jwtService = jwtService;
    }
    public void requireAdmin(HttpServletRequest req){
        try {
            var auth = req.getHeader("Authorization");
            System.out.println("Auth: " + auth);
            if(auth==null || !auth.startsWith("Bearer ")) throw new RuntimeException("no token");
            String token = auth.substring(7);
            System.out.println("Token: " + token);
            String role = jwtService.extractRoles(token).getFirst();
            System.out.println("Role: " + role);
            if(!"ROLE_ADMIN".equals(role)) throw new RuntimeException("forbidden");
        } catch(Exception e){
            throw new RuntimeException("forbidden");
        }
    }
}

