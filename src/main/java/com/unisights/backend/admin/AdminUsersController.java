package com.unisights.backend.admin;

import com.unisights.backend.security.AdminGuard;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@SecurityRequirement(name = "bearer-jwt")
public class AdminUsersController {
    private final JdbcTemplate j; private final AdminGuard guard;
    public AdminUsersController(JdbcTemplate j, AdminGuard g){ this.j=j; this.guard=g; }

    @GetMapping

    public List<Map<String,Object>> list(@RequestParam(required=false) String role, HttpServletRequest req){
        guard.requireAdmin(req);
        if(role==null || role.isBlank()){
            return j.queryForList("select id,email,role,is_active,created_at from users order by created_at desc limit 200");
        }
        return j.queryForList("select id,email,role,is_active,created_at from users where role=? order by created_at desc limit 200", role);
    }

    @PostMapping("/{id}/toggle")
    public void toggle(@PathVariable Long id, HttpServletRequest req){
        guard.requireAdmin(req);
        j.update("update users set is_active = not is_active where id=?", id);
        j.update("insert into audit_log(actor_user_id, action, details) values (null,'USER_TOGGLE',jsonb_build_object('id',?))", id);
    }
}
