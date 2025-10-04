package com.unisights.backend.controller;

import com.unisights.backend.mail.MailService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/apps")
public class ApplicationController {

    private final JdbcTemplate j;

    private final MailService mail;

    public ApplicationController(JdbcTemplate j, MailService mail) {
        this.j = j;
        this.mail = mail;
    }

    private static final Long CURRENT_USER_ID = 1L; // TODO: replace with JWT later

    // 1) Create draft from programId, auto-make checklist from program.reqs->docs
    record CreateReq(Long programId) {
    }

    record ChecklistItem(Long id, String name, String status, boolean required, String comment) {
    }

    record AppView(Long id, String status, Long programId, String programTitle, String university,
                   List<ChecklistItem> checklist) {
    }

    @PostMapping
    public AppView create(@RequestBody CreateReq req) {
        // create row
        Long appId = j.queryForObject(
                "insert into applications(student_id, program_id) values (?, ?) returning id",
                Long.class, CURRENT_USER_ID, req.programId());

        // build checklist from program.reqs.docs JSON array (if present)
        String docs = j.query("select reqs->'docs' as docs from programs where id=?",
                rs -> rs.next() ? rs.getString("docs") : "[]", req.programId());
        // very safe parse: strip [ ] and quotes for today
        if (docs != null && docs.length() > 2) {
            for (String item : docs.replace("[", "").replace("]", "").replace("\"", "").split(",")) {
                String name = item.trim();
                if (!name.isEmpty()) {
                    j.update("insert into checklist_items(application_id, name, required) values (?,?,true)", appId, name);
                }
            }
        }

        j.update("insert into app_events(application_id, event) values (?,?)", appId, "CREATED");

        return detail(appId);
    }

    // 2) Read application (status + checklist + program summary)
    @GetMapping("/{id}")
    public AppView get(@PathVariable Long id) {
        return detail(id);
    }

    private AppView detail(Long id) {
        Map<String, Object> head = j.queryForMap("""
      select a.id, a.status, a.program_id, p.title, u.name as university
      from applications a
      join programs p on p.id = a.program_id
      join universities u on u.id = p.university_id
      where a.id=? and a.student_id=?
    """, id, CURRENT_USER_ID);

        List<ChecklistItem> items = j.query("""
      select id, name, status, required, coalesce(comment,'') as comment
      from checklist_items where application_id=? order by id
    """, (rs, i) -> new ChecklistItem(
                rs.getLong("id"), rs.getString("name"), rs.getString("status"),
                rs.getBoolean("required"), rs.getString("comment")), id);

        return new AppView(
                ((Number) head.get("id")).longValue(),
                (String) head.get("status"),
                ((Number) head.get("program_id")).longValue(),
                (String) head.get("title"),
                (String) head.get("university"),
                items
        );
    }

    // 3) Toggle a checklist item PENDING/DONE
    @PostMapping("/{id}/items/{itemId}/status")
    public void setItemStatus(@PathVariable Long id, @PathVariable Long itemId, @RequestParam String status) {
        j.update("""
      update checklist_items set status=? where id=? and application_id=?
    """, status, itemId, id);
    }

    // 4) Submit application (status → SUBMITTED + event)
    @PostMapping("/{id}/submit")
    public void submit(@PathVariable Long id) {
        j.update("update applications set status='SUBMITTED' where id=? and student_id=?", id, CURRENT_USER_ID);
        j.update("insert into app_events(application_id, event) values (?,?)", id, "SUBMITTED");
        mail.send("consult@demo.com","New Application Submitted",
                "Student #1 submitted app "+id);
    }

    // 5) Timeline
    @GetMapping("/{id}/timeline")
    public List<Map<String, Object>> timeline(@PathVariable Long id) {
        return j.queryForList("select event, created_at from app_events where application_id=? order by created_at", id);
    }

}