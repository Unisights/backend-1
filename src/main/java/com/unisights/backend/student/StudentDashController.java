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
    public List<Map<String, Object>> getApplications(
            @RequestParam(required=false) String status,
            @RequestParam(required=false) String country,
            @RequestParam(required=false) String degree,
            @RequestParam(required=false) Integer feeMax,
            @RequestParam(required=false) String q,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size) {

        int limit = Math.min(Math.max(size, 1), 50);
        int offset = page * limit;

        StringBuilder sql = new StringBuilder("""
        select a.id, p.title, u.name as university, u.country, p.degree, a.status, a.created_at 
        from applications a
        join programs p on p.id = a.program_id
        join universities u on u.id = p.university_id
        where a.student_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(CURRENT_STUDENT);

        if (status != null && !status.isBlank()) {
            sql.append(" and lower(a.status) = lower(?)");
            params.add(status);
        }

        if (country != null && !country.isBlank()) {
            sql.append(" and lower(u.country) = lower(?)");
            params.add(country);
        }

        if (degree != null && !degree.isBlank()) {
            sql.append(" and lower(p.degree) = lower(?)");
            params.add(degree);
        }

        if (feeMax != null) {
            sql.append(" and p.fee <= ?");
            params.add(feeMax);
        }

        if (q != null && !q.isBlank()) {
            sql.append(" and lower(p.title) like lower(?)");
            params.add("%" + q + "%");
        }

        sql.append(" limit ? offset ?");
        params.add(limit);
        params.add(offset);

        return j.queryForList(sql.toString(), params.toArray());
    }
}
