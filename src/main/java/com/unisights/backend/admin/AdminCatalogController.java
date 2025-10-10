package com.unisights.backend.admin;

import com.unisights.backend.security.AdminGuard;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/catalog")
@SecurityRequirement(name = "bearer-jwt")
public class AdminCatalogController {
    private final JdbcTemplate j; private final AdminGuard guard;
    public AdminCatalogController(JdbcTemplate j, AdminGuard g){ this.j=j; this.guard=g; }

    // Universities
    @GetMapping("/universities")
    public List<Map<String,Object>> uniList(HttpServletRequest req){
        guard.requireAdmin(req);
        return j.queryForList("select id,name,country,city from universities order by name limit 200");
    }
    record UniReq(String name,String country,String city){}
    @PostMapping("/universities")
    public void uniCreate(@RequestBody UniReq r, HttpServletRequest req){
        guard.requireAdmin(req);
        j.update("insert into universities(name,country,city) values (?,?,?)", r.name(), r.country(), r.city());
    }

    // Programs
    @Cacheable(value = "programs",  key = "#universityId")
    @GetMapping("/programs")
    public List<Map<String,Object>> progList(@RequestParam(required=false) Long universityId, HttpServletRequest req){
        System.out.println("🔍 EXECUTING DATABASE QUERY - Cache MISS!");
        guard.requireAdmin(req);
        if(universityId==null) return j.queryForList("""
      select p.id,p.title,p.degree,p.fee,u.name as university from programs p join universities u on u.id=p.university_id order by p.id desc limit 200
    """);
        return j.queryForList("""
      select p.id,p.title,p.degree,p.fee,u.name as university from programs p join universities u on u.id=p.university_id where u.id=? order by p.id desc limit 200
    """, universityId);
    }
    record ProgReq(Long universityId,String title,String degree,Integer fee){}
    @PostMapping("/programs")
    public void progCreate(@RequestBody ProgReq r, HttpServletRequest req){
        guard.requireAdmin(req);
        j.update("insert into programs(university_id,title,degree,fee) values (?,?,?,?)",
                r.universityId(), r.title(), r.degree(), r.fee());
    }
}