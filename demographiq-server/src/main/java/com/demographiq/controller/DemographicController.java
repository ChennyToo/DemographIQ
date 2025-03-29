package com.demographiq.controller;

import com.demographiq.service.ArcGISService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demographics")
public class DemographicController {

    private final ArcGISService arcGISService;

    @Autowired
    public DemographicController(ArcGISService arcGISService) {
        this.arcGISService = arcGISService;
    }

    @GetMapping("/enrich")
    public ResponseEntity<String> enrichLocation(
            @RequestParam String address) {
        return ResponseEntity.ok(arcGISService.enrichAddress(address));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Demographic Server is running");
    }
}