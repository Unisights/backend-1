package com.unisights.backend.catalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = (Math.max(page, 1) - 1) * limit;

        String orderBy = switch (sort) {
            case "fee" -> "p.fee";
            case "degree" -> "p.degree";
            case "university" -> "u.name";
            default -> "p.title";
        };

        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        // ✅ Start building SQL dynamically
        StringBuilder sql = new StringBuilder("""
            select p.id, u.name university, u.country, p.title, p.degree, p.fee
            from programs p
            join universities u on u.id = p.university_id
            where 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (country != null && !country.isBlank()) {
            sql.append(" and lower(u.country) = lower(?)");
            params.add(country);
        }

        if (degree != null && !degree.isBlank()) {
            sql.append(" and lower(p.degree) = lower(?)");
            params.add(degree);
        }

        if (feeMax != null) {
            sql.append(" and p.fee <= ?");
            params.add(feeMax);
        }

        if (q != null && !q.isBlank()) {
            sql.append(" and lower(p.title) like lower(?)");
            params.add("%" + q + "%");
        }

        sql.append(" order by ").append(orderBy).append(" ").append(direction);
        sql.append(" limit ? offset ?");
        params.add(limit);
        params.add(offset);

        return j.query(sql.toString(),
                (rs, i) -> new ProgramView(
                        rs.getLong("id"),
                        rs.getString("university"),
                        rs.getString("country"),
                        rs.getString("title"),
                        rs.getString("degree"),
                        (Integer) rs.getObject("fee")),
                params.toArray());

    }


}