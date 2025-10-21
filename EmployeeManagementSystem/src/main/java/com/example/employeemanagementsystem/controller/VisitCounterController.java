package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.service.VisitCounter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
public class VisitCounterController {

    private final VisitCounter visitCounter;

    @Autowired
    public VisitCounterController(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Get the number of visits for a specific URL")
    @GetMapping
    public ResponseEntity<Long> getVisitCount(
        @Parameter(description = "URL to get visit count for", example = "/api/employees",
            allowReserved = true)
        @RequestParam String url) {
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        long count = visitCounter.getVisitCount(decodedUrl);
        return ResponseEntity.ok(count);
    }
}