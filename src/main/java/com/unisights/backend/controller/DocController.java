package com.unisights.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.nio.file.StandardCopyOption;


@RestController
@RequestMapping("/api/v1/docs")
public class DocController {
    private final Path root = Paths.get("uploads");

    public DocController() throws IOException {
        Files.createDirectories(root);
    }

    @PostMapping("/upload")
    public Map<String,String> upload(@RequestParam Long appId, @RequestParam MultipartFile file) throws IOException {
        String fn = appId + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(fn), StandardCopyOption.REPLACE_EXISTING);
        return Map.of("filename", fn, "status","OK");
    }

    @GetMapping("/{appId}")
    public List<String> list(@PathVariable Long appId) throws IOException {
        try (var s = Files.list(root)) {
            return s.filter(p -> p.getFileName().toString().startsWith(appId+""))
                    .map(p -> p.getFileName().toString()).toList();
        }
    }
}

