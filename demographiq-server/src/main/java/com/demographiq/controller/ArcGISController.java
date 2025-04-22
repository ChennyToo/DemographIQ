package com.demographiq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demographiq.model.EnrichmentRequest;
import com.demographiq.service.ArcGISService;

@RestController
@RequestMapping("/api")
public class ArcGISController {

    private final ArcGISService arcGISService;

    @Autowired
    public ArcGISController(ArcGISService arcGISService) {
        this.arcGISService = arcGISService;
    }

    @PostMapping("/enrich")
    public ResponseEntity<String> enrichLocation(@RequestBody EnrichmentRequest request) {
        return ResponseEntity.ok(arcGISService.enrichLocation(request));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Log the full stack trace
        ex.printStackTrace();
        
        // Return a more informative error message
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Internal Server Error: " + ex.getMessage());
    }
}