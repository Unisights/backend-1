package com.unisights.backend.eligibility;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/eligibility")
public class EligibilityController {

    private final JdbcTemplate j;
    public EligibilityController(JdbcTemplate j) { this.j = j; }


    public record CheckReq(Double gpa, Double ielts, Integer toefl, Integer budget, List<Long> programIds) {}

    public record Result(Long programId, boolean eligible, int score, List<String> reasons) {}

    @PostMapping("/check")
    public List<Result> check(@RequestBody CheckReq req) {
        if (req.programIds() == null || req.programIds().isEmpty()) return List.of();


        String ids = String.join(",", req.programIds().stream().map(String::valueOf).toList());


        var rows = j.query(
                "select p.id, p.min_gpa, p.min_ielts, p.min_toefl, p.fee from programs p where p.id in (" + ids + ")",
                (rs, rowNum) -> new Object[]{
                        rs.getLong("id"),
                        rs.getDouble("min_gpa"),
                        rs.getDouble("min_ielts"),
                        rs.getInt("min_toefl"),
                        rs.getInt("fee")
                }
        );

        List<Result> results = new ArrayList<>();

        for (Object[] row : rows) {
            Long programId = (Long) row[0];
            Double minGpa = (Double) row[1];
            Double minIelts = (Double) row[2];
            Integer minToefl = (Integer) row[3];
            Integer fee = (Integer) row[4];

            List<String> reasons = new ArrayList<>();
            int score = 0;


            if (req.gpa() >= minGpa) score += 25; else reasons.add("GPA too low");
            if (req.ielts() >= minIelts) score += 25; else reasons.add("IELTS too low");
            if (req.toefl() >= minToefl) score += 25; else reasons.add("TOEFL too low");
            if (req.budget() >= fee) score += 25; else reasons.add("Budget too low");

            boolean eligible = reasons.isEmpty();

            results.add(new Result(programId, eligible, score, reasons));
        }

        return results;
    }
}
