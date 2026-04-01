package com.krzsta.urlshortener.dto;

import java.time.LocalDateTime;

public record UrlResponse (
    String originalUrl,
    String shortCode,
    String shortUrl,
    long clicks,
    LocalDateTime createdAt,
    LocalDateTime expiresAt    
) {}
