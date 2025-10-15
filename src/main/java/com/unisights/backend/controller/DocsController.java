package com.unisights.backend.controller;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/docs")
@SecurityRequirement(name = "bearer-jwt")
public class DocsController {
    private final MinioClient s3;
    private final JdbcTemplate j;
    private final String bucket="unisights";
    public DocsController(MinioClient s3, JdbcTemplate j){ this.s3=s3; this.j=j; }

    record PresignReq(Long appId, String filename, String mime, Long size){}
    record PresignRes(String url, String key){}

    @PostMapping("/presign")
    public PresignRes presign(@RequestBody PresignReq r) throws Exception {
        // Basic checks
        try {
            var allowed = List.of("application/pdf", "image/jpeg", "image/png",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            if (r.size() == null || r.size() > 10_000_000) throw new RuntimeException("File too large (>10MB)");
            if (r.mime() == null || !allowed.contains(r.mime())) throw new RuntimeException("Unsupported type");
            String key = r.appId() + "_" + System.currentTimeMillis() + "_" + r.filename();

            var url = s3.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT).bucket(bucket).object(key)
                            .extraQueryParams(Map.of("X-Amz-Meta-OrigName", r.filename()))
                            .build()
            );
            return new PresignRes(url, key);
        } catch (Exception e){
            throw new RuntimeException("Cannot create presign url: " + e.getMessage());
        }
    }

    record SaveReq(Long appId, String key, String filename, String mime, Long size, Long uploadedBy){}
    @PostMapping("/save")
    public void save(@RequestBody SaveReq r){
        j.update("""
      insert into app_files(application_id,storage_key,original_name,mime,size_bytes,uploaded_by)
      values (?,?,?,?,?,?)
    """, r.appId(), r.key(), r.filename(), r.mime(), r.size(), r.uploadedBy());
    }

    @GetMapping("/{appId}")
    public List<Map<String,Object>> list(@PathVariable Long appId,  HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader);
        return j.queryForList("""
      select id, storage_key, original_name, mime, size_bytes, created_at
      from app_files where application_id=? order by created_at desc
    """, appId);
    }

    @GetMapping("/presign-get/{id}")
    public Map<String,String> presignGet(@PathVariable Long id) throws Exception {
        var row = j.queryForMap("select storage_key, mime from app_files where id=?", id);
        String key = (String)row.get("storage_key");
        String url = s3.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(key).build());
        return Map.of("url", url);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws Exception {
        var row = j.queryForMap("select storage_key from app_files where id=?", id);
        String key = (String)row.get("storage_key");
        s3.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        j.update("delete from app_files where id=?", id);
    }
}
