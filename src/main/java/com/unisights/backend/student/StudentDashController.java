package com.unisights.backend.student;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentDashController {

    private final JdbcTemplate j;

    public StudentDashController(JdbcTemplate j) {
        this.j = j;
    }

    private static final long CURRENT_STUDENT = 1L;

    @GetMapping("/apps")
    public List<Map<String,Object>> myApps(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updated_at") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = (Math.max(page, 1) - 1) * limit;

        String orderBy;
        switch (sort) {
            case "status" -> orderBy = "a.status";
            case "program" -> orderBy = "p.title";
            case "created_at" -> orderBy = "a.created_at";
            default -> orderBy = "updated_at";
        }

        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        String sql = """
            select a.id as app_id, a.status, a.created_at,
                   p.title as program_title, u.name as university,
                   coalesce((select max(e.created_at) from app_events e where e.application_id=a.id), a.created_at) as updated_at
            from applications a
            join programs p on p.id = a.program_id
            join universities u on u.id = p.university_id
            where a.student_id = ?
              and (?::text is null or a.status = ?)
              and (?::text is null or p.title ILIKE ?)
            order by """ + orderBy + " " + direction + """
            limit ? offset ?
        """;

        return j.queryForList(
                sql,
                CURRENT_STUDENT,
                status, status,
                q, q == null ? null : "%" + q + "%",
                limit, offset
        );
    }
}

