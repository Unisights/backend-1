package com.unisights.backend.student;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/student")
public class StudentDashController {

    private final JdbcTemplate j;
    public StudentDashController(JdbcTemplate j){ this.j = j; }

    private static final long CURRENT_STUDENT = 1L; // TODO JWT later

    @GetMapping("/apps")
    public List<Map<String,Object>> myApps(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q) {

        String sql = """
      select a.id as app_id, a.status, a.created_at,
             p.title as program_title, u.name as university,
             coalesce((select max(e.created_at)
                       from app_events e
                       where e.application_id = a.id),
                      a.created_at) as updated_at
      from applications a
      join programs p on p.id = a.program_id
      join universities u on u.id = p.university_id
      where a.student_id = ?
        and (? is null or a.status = ?)
        and (? is null or lower(p.title) like lower('%' || ? || '%'))
      order by updated_at desc
      limit 200
    """;

        return j.queryForList(sql, CURRENT_STUDENT, status, status, q, q);
    }
}

