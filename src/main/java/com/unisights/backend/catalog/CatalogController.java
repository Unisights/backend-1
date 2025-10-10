package com.unisights.backend.catalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

record ProgramView(Long id,String university,String country,String title,String degree,Integer fee){}

@RestController @RequestMapping("/api/programs")
public class CatalogController {
    private final JdbcTemplate j;
    public CatalogController(JdbcTemplate j){this.j=j;}
    @GetMapping
    public List<ProgramView> list(
            @RequestParam(required=false) String country,
            @RequestParam(required=false) String degree,
            @RequestParam(required=false) Integer feeMax,
            @RequestParam(required=false) String q,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="title") String sort,
            @RequestParam(defaultValue="asc") String dir
    ){
        int limit = Math.min(Math.max(size,1),50);
        int offset = (Math.max(page,1)-1)*limit;
        String orderBy = switch (sort){
            case "fee" -> "p.fee";
            case "degree" -> "p.degree";
            case "university" -> "u.name";
            default -> "p.title";
        };
        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        String sql = """
    select p.id, u.name university, u.country, p.title, p.degree, p.fee
    from programs p join universities u on u.id=p.university_id
    where (? is null or lower(u.country)=lower(?))
      and (? is null or lower(p.degree)=lower(?))
      and (? is null or p.fee <= ?)
      and (? is null or lower(p.title) like lower('%'?'%'))
    order by """ + orderBy + " " + direction + """
    limit ? offset ?
  """;

        return j.query(sql, (rs,i)-> new ProgramView(
                        rs.getLong("id"), rs.getString("university"),
                        rs.getString("country"), rs.getString("title"),
                        rs.getString("degree"), (Integer)rs.getObject("fee")),
                country,country,degree,degree,feeMax,feeMax,q,q, limit, offset);
    }

}