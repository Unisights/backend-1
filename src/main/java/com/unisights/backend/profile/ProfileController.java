package com.unisights.backend.profile;


import com.unisights.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final JdbcTemplate j;
    private final JwtService jwtService;

    public ProfileController(JdbcTemplate j, JwtService jwtService) {
        this.j = j;
        this.jwtService = jwtService;
    }

    private Long currentUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");
        String token = header.substring(7);
        Long userId = jwtService.extractUserId(token);
        if (userId == null)
            throw new RuntimeException("user_id claim missing");
        return userId;
    }

    @GetMapping
    public Map<String,Object> get(HttpServletRequest request) {
        Long userId = currentUserId(request);
        System.out.println("userId=" + userId);
        return j.queryForMap("""
      select user_id, full_name, country, gpa, ielts, toefl, budget
      from student_profiles where user_id=?
    """, userId);
    }

    @PutMapping
    public void update(HttpServletRequest request, @RequestBody Map<String,Object> body) {
        Long userId = currentUserId(request);
        j.update("""
      update student_profiles set
        full_name = coalesce(cast(? as text), full_name),
        country   = coalesce(cast(? as text), country),
        gpa       = coalesce(cast(? as numeric), gpa),
        ielts     = coalesce(cast(? as numeric), ielts),
        toefl     = coalesce(cast(? as int), toefl),
        budget    = coalesce(cast(? as int), budget),
        updated_at = now()
      where user_id=?
    """, body.get("full_name"), body.get("country"), body.get("gpa"),
                body.get("ielts"), body.get("toefl"), body.get("budget"),
                userId);
    }


    @PostMapping
    public String create(HttpServletRequest request, @RequestBody Map<String,Object> body) {
        Long userId = currentUserId(request);

        // Check if profile already exists
        Integer count = j.queryForObject(
                "SELECT COUNT(*) FROM student_profiles WHERE user_id = ?",
                Integer.class, userId);

        if (count > 0) {
            return "Profile already exists for this user";
        }

        // Insert new profile
        j.update("""
        INSERT INTO student_profiles (user_id, full_name, country, gpa, ielts, toefl, budget, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, now())
    """, userId,
                body.get("full_name"),
                body.get("country"),
                body.get("gpa"),
                body.get("ielts"),
                body.get("toefl"),
                body.get("budget"));

        return "Profile created successfully";
    }



}

