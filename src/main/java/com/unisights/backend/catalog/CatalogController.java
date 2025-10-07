package com.unisights.backend.catalog;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

record ProgramView(Long id, String university, String country, String title, String degree, Integer fee) {}

@RestController
@RequestMapping("/api/programs")
public class CatalogController {

    private final JdbcTemplate j;

    public CatalogController(JdbcTemplate j) {
        this.j = j;
    }


    @GetMapping
    public List<ProgramView> list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) Integer feeMax,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = (Math.max(page, 1) - 1) * limit;

        String orderBy = switch (sort) {
            case "fee" -> "p.fee";
            case "degree" -> "p.degree";
            case "university" -> "u.name";
            default -> "p.title";
        };
        String direction = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        String sql = """
            SELECT p.id, u.name AS university, u.country, p.title, p.degree, p.fee
            FROM programs p
            JOIN universities u ON u.id = p.university_id
            WHERE (? IS NULL OR LOWER(u.country) = LOWER(?))
              AND (? IS NULL OR LOWER(p.degree) = LOWER(?))
              AND (? IS NULL OR p.fee <= ?)
              AND (? IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', ?, '%')))
            ORDER BY """ + orderBy + " " + direction + """
            LIMIT ? OFFSET ?
        """;

        return j.query(sql, (rs, i) -> new ProgramView(
                        rs.getLong("id"),
                        rs.getString("university"),
                        rs.getString("country"),
                        rs.getString("title"),
                        rs.getString("degree"),
                        (Integer) rs.getObject("fee")
                ),
                country, country,
                degree, degree,
                feeMax, feeMax,
                q, q,
                limit, offset);
    }

  
    @Cacheable("countries")
    @GetMapping("/countries")
    public List<String> listCountries() {
        String sql = "SELECT DISTINCT country FROM universities ORDER BY country";
        return j.queryForList(sql, String.class);
    }

   
    @Cacheable("degrees")
    @GetMapping("/degrees")
    public List<String> listDegrees() {
        String sql = "SELECT DISTINCT degree FROM programs ORDER BY degree";
        return j.queryForList(sql, String.class);
    }

  
    @Cacheable("programCount")
    @GetMapping("/count")
    public Integer getProgramCount() {
        String sql = "SELECT COUNT(*) FROM programs";
        return j.queryForObject(sql, Integer.class);
    }
}
