package com.unisights.applications;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/apps")
public class ApplicationController {

    private final JdbcTemplate j;

    public ApplicationController(JdbcTemplate j) {
        this.j = j;
    }

    private static final Long CURRENT_USER_ID = 1L; // TODO: replace with JWT later


    record CreateReq(Long programId) {}
    record ChecklistItem(Long id, String name, String status, boolean required, String comment) {}
    record AppView(Long id, String status, Long programId, String programTitle, String university, List<ChecklistItem> checklist) {}


    @PostMapping
    public AppView create(@RequestBody CreateReq req){
        // insert application row
        Long appId = j.queryForObject(
                "insert into applications(student_id, program_id) values (?, ?) returning id",
                Long.class, CURRENT_USER_ID, req.programId());


        String docs = j.query(
                "select reqs->'docs' as docs from programs where id=?",
                rs -> rs.next() ? rs.getString("docs") : "[]",
                req.programId());

        if (docs != null && docs.length() > 2) {
            for (String item : docs.replace("[","").replace("]","").replace("\"","").split(",")) {
                String name = item.trim();
                if (!name.isEmpty()) {
                    j.update("insert into checklist_items(application_id, name, required) values (?,?,true)", appId, name);
                }
            }
        }


        j.update("insert into app_events(application_id, event) values (?,?)", appId, "CREATED");

        return detail(appId);
    }

    // 2) Read application
    @GetMapping("/{id}")
    public AppView get(@PathVariable Long id){
        return detail(id);
    }


    @PostMapping("/{appId}/checklist/{itemId}/toggle")
    public ChecklistItem toggle(@PathVariable Long appId, @PathVariable Long itemId){
        // toggle status
        j.update("update checklist_items set status = case when status='DONE' then 'PENDING' else 'DONE' end where id=? and application_id=?", itemId, appId);

        return j.queryForObject(
                "select id, name, status, required, comment from checklist_items where id=? and application_id=?",
                (rs, rowNum) -> new ChecklistItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("status"),
                        rs.getBoolean("required"),
                        rs.getString("comment")
                ), itemId, appId);
    }


    @PostMapping("/{id}/submit")
    public AppView submit(@PathVariable Long id){
        j.update("update applications set status='SUBMITTED' where id=? and student_id=?", id, CURRENT_USER_ID);
        j.update("insert into app_events(application_id, event) values (?,?)", id, "SUBMITTED");
        return detail(id);
    }

    // 5) Timeline of events
    @GetMapping("/{id}/timeline")
    public List<Map<String,Object>> timeline(@PathVariable Long id){
        return j.query("select event, created_at from app_events where application_id=? order by created_at asc",
                (rs, rowNum) -> Map.of(
                        "event", rs.getString("event"),
                        "createdAt", rs.getTimestamp("created_at")
                ), id);
    }


    private AppView detail(Long id){
        Map<String,Object> head = j.queryForMap("""
            select a.id, a.status, a.program_id, p.title, u.name as university
            from applications a
            join programs p on p.id = a.program_id
            join universities u on u.id = p.university_id
            where a.id=? and a.student_id=?
            """, id, CURRENT_USER_ID);

        List<ChecklistItem> checklist = j.query(
                "select id, name, status, required, comment from checklist_items where application_id=?",
                (rs, rowNum) -> new ChecklistItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("status"),
                        rs.getBoolean("required"),
                        rs.getString("comment")
                ), id);

        return new AppView(
                (Long) head.get("id"),
                (String) head.get("status"),
                (Long) head.get("program_id"),
                (String) head.get("title"),
                (String) head.get("university"),
                checklist
        );
    }
}
