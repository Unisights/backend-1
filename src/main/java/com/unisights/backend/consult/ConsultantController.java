package com.unisights.backend.consult;

import com.unisights.backend.mail.MailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/consult")
@SecurityRequirement(name = "bearer-jwt")
public class ConsultantController {

    private final JdbcTemplate j;
    private final MailService mail;

    public ConsultantController(JdbcTemplate j, MailService mail) {
        this.j = j;
        this.mail = mail;
    }

    private static final long DEMO_CONSULTANT_ID = 100L; // TODO: replace with real auth later
    @GetMapping("/queue")
    public List<Map<String, Object>> queue(
            @RequestParam(required = false) String status, // SUBMITTED / REVIEW / ACCEPTED / REJECTED
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = (Math.max(page, 1) - 1) * limit;

        // ✅ Validate and map sort fields to prevent SQL injection
        String orderBy = switch (sort) {
            case "status" -> "a.status";
            case "program_title" -> "p.title";
            case "university" -> "u.name";
            default -> "a.created_at";
        };

        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        // ✅ Start building SQL dynamically
        StringBuilder sql = new StringBuilder("""
    select a.id as app_id, a.status, a.created_at,
           p.title as program_title, u.name as university
    from applications a
    join programs p on p.id = a.program_id
    join universities u on u.id = p.university_id
    where 1=1
""");

        List<Object> params = new ArrayList<>();

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
