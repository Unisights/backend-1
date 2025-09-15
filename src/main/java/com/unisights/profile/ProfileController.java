package com.unisights.backend.profile;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final JdbcTemplate j;
    public ProfileController(JdbcTemplate j){ this.j = j; }

    private static final Long CURRENT_USER_ID = 1L; // TODO: replace with JWT later

    @GetMapping
    public Map<String,Object> get() {
        return j.queryForMap("""
      select user_id, full_name, country, gpa, ielts, toefl, budget
      from student_profiles where user_id=?
    """, CURRENT_USER_ID);
    }

    @PutMapping
    public void update(@RequestBody Map<String,Object> body) {
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
                CURRENT_USER_ID);
    }
}
