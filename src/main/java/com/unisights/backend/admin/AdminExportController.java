package com.unisights.backend.admin;

import com.unisights.backend.security.AdminGuard;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/export")
@SecurityRequirement(name = "bearer-jwt")
public class AdminExportController {
    private final JdbcTemplate j; private final AdminGuard guard;
    public AdminExportController(JdbcTemplate j, AdminGuard g){ this.j=j; this.guard=g; }

    @GetMapping(value="/applications.csv", produces="text/csv")
    public String appsCsv(@RequestParam(required=false) String status, HttpServletRequest req){
        guard.requireAdmin(req);
        var rows = (status==null || status.isBlank())
                ? j.queryForList("""
          select a.id, a.status, u.name as university, p.title as program, a.created_at
          from applications a join programs p on p.id=a.program_id join universities u on u.id=p.university_id
          order by a.created_at desc
        """)
                : j.queryForList("""
          select a.id, a.status, u.name as university, p.title as program, a.created_at
          from applications a join programs p on p.id=a.program_id join universities u on u.id=p.university_id
          where a.status=? order by a.created_at desc
        """, status);

        var header = "id,status,university,program,created_at";
        var body = rows.stream().map(r ->
                r.get("id")+","+r.get("status")+","+
                        quote(r.get("university"))+","+
                        quote(r.get("program"))+","+
                        r.get("created_at")
        ).collect(Collectors.joining("\n"));

        return header + "\n" + body;
    }

    private String quote(Object s){
        if(s==null) return "";
        String t = s.toString();
        if(t.contains(",") || t.contains("\"")) return "\""+t.replace("\"","\"\"")+"\"";
        return t;
    }
}
