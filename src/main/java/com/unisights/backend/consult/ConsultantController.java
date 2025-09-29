package com.unisights.backend.consult;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/consult")
public class ConsultantController {

    private final JdbcTemplate j;

    public ConsultantController(JdbcTemplate j) {
        this.j = j;
    }

    @GetMapping("/queue")
    public List<Map<String, Object>> queue(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q) {

        String sql = """
      select a.id as app_id, a.status, a.created_at,
             p.title as program_title, u.name as university
      from applications a
      join programs p on p.id = a.program_id
      join universities u on u.id = p.university_id
      where (? is null or a.status = ?)
        and (? is null or lower(p.title) like lower('%' || ? || '%'))
      order by a.created_at desc
      limit 200
    """;

        return j.queryForList(sql, status, status, q, q);
    }
}
