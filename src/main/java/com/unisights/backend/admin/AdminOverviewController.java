package com.unisights.backend.admin;

import com.unisights.backend.security.AdminGuard;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/overview")
@SecurityRequirement(name = "bearer-jwt")
public class AdminOverviewController {
    private final JdbcTemplate j;
    private final AdminGuard guard;

    public AdminOverviewController(JdbcTemplate j, AdminGuard g) {
        this.j = j;
        this.guard = g;
    }

    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping
    public Map<String, Object> kpis(HttpServletRequest req) {
        guard.requireAdmin(req);
        var totalStudents = j.queryForObject("select count(*) from users where role='STUDENT'", Long.class);
        var totalConsultants = j.queryForObject("select count(*) from users where role='CONSULTANT'", Long.class);
        var totalPrograms = j.queryForObject("select count(*) from programs", Long.class);
        var draft = j.queryForObject("select count(*) from applications where status='DRAFT'", Long.class);
        var submitted = j.queryForObject("select count(*) from applications where status='SUBMITTED'", Long.class);
        var review = j.queryForObject("select count(*) from applications where status='REVIEW'", Long.class);
        var accepted = j.queryForObject("select count(*) from applications where status='ACCEPTED'", Long.class);
        var rejected = j.queryForObject("select count(*) from applications where status='REJECTED'", Long.class);
        return Map.of(
                "students", totalStudents, "consultants", totalConsultants, "programs", totalPrograms,
                "apps", Map.of("draft", draft, "submitted", submitted, "review", review, "accepted", accepted, "rejected", rejected)
        );
    }

}