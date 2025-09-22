package com.unisights.backend.profile;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/eligibility")
public class EligibilityController {
    private final JdbcTemplate j;
    public EligibilityController(JdbcTemplate j){ this.j = j; }

    public record CheckReq(Double gpa, Double ielts, Integer toefl, Integer budget, List<Long> programIds){}
    public record Result(Long programId, boolean eligible, int score, List<String> reasons){}

    @PostMapping("/check")
    public List<Result> check(@RequestBody CheckReq req){
        if (req.programIds()==null || req.programIds().isEmpty()) return List.of();

        String ids = String.join(",", req.programIds().stream().map(String::valueOf).toList());
        var rows = j.query("""
      select p.id, p.min_gpa, p.min_ielts, p.min_toefl, p.fee
      from programs p
      where p.id in (""" + ids + ")",
                (rs, i) -> new Object[]{
                        rs.getLong("id"),
                        rs.getObject("min_gpa") != null ? ((BigDecimal) rs.getObject("min_gpa")).doubleValue() : 0.0,
                        rs.getObject("min_ielts") != null ? ((BigDecimal) rs.getObject("min_ielts")).doubleValue() : 0.0,
                        rs.getObject("min_toefl") != null ? (Integer) rs.getObject("min_toefl") : 0,
                        rs.getObject("fee") != null ? (Integer) rs.getObject("fee") : 0
                });

        List<Result> out = new ArrayList<>();
        for (Object[] r : rows) {
            Long id = (Long) r[0];
            double minG = r[1]==null?0.0:((Number)r[1]).doubleValue();
            double minI = r[2]==null?0.0:((Number)r[2]).doubleValue();
            int    minT = r[3]==null?0:((Number)r[3]).intValue();
            int      fee= r[4]==null?0:((Number)r[4]).intValue();

            int score = 0; List<String> reasons = new ArrayList<>();
            if (req.gpa()!=null   && req.gpa()   >= minG) score+=30; else reasons.add("GPA below minimum");
            if (req.ielts()!=null && req.ielts() >= minI) score+=30; else if (minI>0) reasons.add("IELTS below minimum");
            if (req.toefl()!=null && req.toefl() >= minT) score+=20; else if (minT>0) reasons.add("TOEFL below minimum");
            if (req.budget()!=null&& req.budget()>= fee)  score+=20; else if (fee>0) reasons.add("Budget below fee");

            out.add(new Result(id, reasons.isEmpty(), score, reasons));
        }
        return out;
    }
}

