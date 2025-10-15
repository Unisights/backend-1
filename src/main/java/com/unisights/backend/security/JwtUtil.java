//package com.unisights.backend.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
////    private final Key key;
////    public JwtUtil(@Value("${jwt.secret}") String secret) {
////        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
////    }
//
//
//    public String generateToken(String role, Long userId){
//        return Jwts.builder()
//                .setSubject(role)
//                .claim("uid", userId)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis()+86400000)) // 1 day
//                .signWith(key)
//                .compact();
//    }
//
//    public Jws<Claims> parse(String token){
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//    }
//}
