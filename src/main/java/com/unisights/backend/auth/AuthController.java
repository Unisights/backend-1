package com.unisights.backend.auth;

import com.unisights.backend.security.JwtUtil;
import com.unisights.backend.user.User;
import com.unisights.backend.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwt;
    private final UserRepository repo;
    private final PasswordEncoder enc;

    public AuthController(JwtUtil jwt, UserRepository repo, PasswordEncoder enc) {
        this.jwt = jwt;
        this.repo = repo;
        this.enc = enc;
    }
    
    public static record SignupReq(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String role
    ) {}

    public static record LoginReq(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public static record LoginRes(
            String token,
            String role,
            Long userId
    ) {}

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupReq req) {
        Optional<User> existingUser = repo.findByEmail(req.email());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        User.Role roleEnum;
        try {
            roleEnum = User.Role.valueOf(req.role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + req.role);
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPassword_hash(enc.encode(req.password()));
        user.setRole(roleEnum);
        repo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {
        Optional<User> userOpt = repo.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();

        if (!enc.matches(req.password(), user.getPassword_hash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwt.generateToken(user.getRole().name(), user.getId());
        LoginRes response = new LoginRes(token, user.getRole().name(), user.getId());
        return ResponseEntity.ok(response);
    }
}
