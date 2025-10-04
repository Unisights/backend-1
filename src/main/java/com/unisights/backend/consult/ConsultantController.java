package com.unisights.backend.consult;

import com.unisights.backend.mail.MailService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/consult")
public class ConsultantController {

    private final JdbcTemplate j;
    private final MailService mail;

    public ConsultantController(JdbcTemplate j, MailService mail) {
        this.j = j;
        this.mail = mail;
    }

    private static final long DEMO_CONSULTANT_ID = 100L; // TODO: replace with real auth later
    @GetMapping("/queue")
    public List<Map<String,Object>> queue(
            @RequestParam(required=false) String status, // SUBMITTED/REVIEW/ACCEPTED/REJECTED
            @RequestParam(required=false) String q) {

        String sql = """
    select a.id as app_id, a.status, a.created_at,
           p.title as program_title, u.name as university
    from applications a
    join programs p on p.id=a.program_id
    join universities u on u.id=p.university_id
    where (?::text is null or a.status = ?)
      and (?::text is null or p.title ILIKE ?)
    order by a.created_at desc
    limit 200
""";
        return j.queryForList(sql, status, status, q, q == null ? null : "%" + q + "%");
    }

    public record ReviewReq(Long applicationId, String decision, String feedback) {

    }

    @PostMapping("/review")
    public void review(@RequestBody ReviewReq r) {

        j.update("""
            insert into reviews(application_id, consultant_id, decision, feedback)
            values (?,?,?,?)
        """, r.applicationId(), DEMO_CONSULTANT_ID, r.decision(), r.feedback());

        String next = switch (r.decision()) {
            case "APPROVE"   -> "ACCEPTED";
            case "REJECT"    -> "REJECTED";
            case "NEEDS_FIX" -> "REVIEW";
            default          -> "REVIEW";
        };
        j.update("update applications set status = ? where id = ?", next, r.applicationId());
        j.update("insert into app_events(application_id, event) values (?, ?)",
                r.applicationId(), "DECISION_" + next);

        mail.send("consult@demo.com","Application Decision",
                "Your application "+r.applicationId()+" is "+next);

    }
}
