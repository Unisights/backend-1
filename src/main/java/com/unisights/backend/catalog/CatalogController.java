package com.unisights.backend.catalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

record ProgramView(Long id,String university,String country,String title,String degree,Integer fee){}

@RestController @RequestMapping("/api/programs")
public class CatalogController {
    private final JdbcTemplate j;
    public CatalogController(JdbcTemplate j){this.j=j;}
    @GetMapping public List<ProgramView> list(){
        String sql = """
      select p.id, u.name university, u.country, p.title, p.degree, p.fee
      from programs p join universities u on u.id=p.university_id limit 100
    """;
        return j.query(sql,(rs,i)-> new ProgramView(
                rs.getLong(1), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getString(5), (Integer)rs.getObject(6)));
    }
}