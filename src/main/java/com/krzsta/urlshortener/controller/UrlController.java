package com.krzsta.urlshortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;

import com.krzsta.urlshortener.dto.ShortenRequest;
import com.krzsta.urlshortener.service.UrlService;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<String> shorten(@RequestBody ShortenRequest request) {
        String shortCode = urlService.shorten(request);
        String shortUrl = "http://localhost:8080/" + shortCode;
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.redirect(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
