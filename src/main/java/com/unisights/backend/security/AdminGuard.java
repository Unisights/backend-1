package com.unisights.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AdminGuard {
    private final JwtUtil jwt;
    public AdminGuard(JwtUtil jwt){ this.jwt = jwt; }
    public void requireAdmin(HttpServletRequest req){
        try {
            var auth = req.getHeader("Authorization");
            System.out.println("Auth: " + auth);
            if(auth==null || !auth.startsWith("Bearer ")) throw new RuntimeException("no token");
            String token = auth.substring(7);
            System.out.println("Token: " + token);
            Jws<Claims> parsed = jwt.parse(token);
            System.out.println("Parsed: " + parsed);
            String role = parsed.getBody().getSubject();
            System.out.println("Role: " + role);
            if(!"ADMIN".equals(role)) throw new RuntimeException("forbidden");
        } catch(Exception e){
            throw new RuntimeException("forbidden");
        }
    }
}

