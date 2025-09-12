package com.unisights.backend.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthControllerJdbc {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    public AuthControllerJdbc(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    public record SignupReq(String email, String password, String role) {}
    public record LoginReq(String email, String password) {}
    public record UserResp(Long id, String email, String role, Instant createdAt) {}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReq req) {
        Integer existing = jdbc.queryForObject("select count(*) from users where email = ?", Integer.class, req.email());
        if (existing != null && existing > 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Email already registered"));

        String hashed = encoder.encode(req.password());
        jdbc.update("insert into users(email,password,role) values (?,?,?)", req.email(), hashed, req.role() == null ? "USER" : req.role());

        UserResp saved = jdbc.queryForObject(
                "select id,email,role,created_at from users where email = ?",
                (rs, rn) -> new UserResp(rs.getLong("id"), rs.getString("email"), rs.getString("role"), rs.getTimestamp("created_at").toInstant()),
                req.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        var list = jdbc.query("select id,email,password,role,created_at from users where email = ?",
                (rs, rn) -> Map.of(
                        "id", rs.getLong("id"),
                        "email", rs.getString("email"),
                        "password", rs.getString("password"),
                        "role", rs.getString("role"),
                        "createdAt", rs.getTimestamp("created_at").toInstant()
                ), req.email());

        if (list.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));

        var row = list.get(0);
        String hashed = (String) row.get("password");
        if (!encoder.matches(req.password(), hashed)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));

        UserResp resp = new UserResp((Long) row.get("id"), (String) row.get("email"), (String) row.get("role"), (Instant) row.get("createdAt"));
        return ResponseEntity.ok(resp);
    }
}
