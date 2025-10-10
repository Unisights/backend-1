package com.unisights.backend.student;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentDashController {
    private final JdbcTemplate j;
    public StudentDashController(JdbcTemplate j){ this.j = j; }

    private static final long CURRENT_STUDENT = 1L; // TODO JWT later

    @GetMapping("/apps")
    public List<Map<String, Object>> myApps(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updated_at") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = (Math.max(page, 1) - 1) * limit;

        // 🔹 Validate sort fields to prevent SQL injection
        String orderBy = switch (sort) {
            case "status" -> "a.status";
            case "program_title" -> "p.title";
            case "university" -> "u.name";
            case "created_at" -> "a.created_at";
            default -> "updated_at";
        };

        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        // 🔹 Build dynamic WHERE clauses safely
        StringBuilder sql = new StringBuilder("""
                select a.id as app_id, a.status, a.created_at,
                       p.title as program_title, u.name as university,
                       coalesce(
                         (select max(e.created_at) from app_events e where e.application_id=a.id),
                         a.created_at
                       ) as updated_at
                from applications a
                join programs p on p.id = a.program_id
                join universities u on u.id = p.university_id
                where a.student_id = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(CURRENT_STUDENT);

        if (status != null && !status.isBlank()) {
            sql.append(" and a.status = ?");
            params.add(status);
        }

        if (q != null && !q.isBlank()) {
            sql.append(" and (p.title ILIKE ? or u.name ILIKE ?)");
            params.add("%" + q + "%");
            params.add("%" + q + "%");
        }

        sql.append(" order by ").append(orderBy).append(" ").append(direction);
        sql.append(" limit ? offset ?");
        params.add(limit);
        params.add(offset);

        return j.queryForList(sql.toString(), params.toArray());
    }
}
