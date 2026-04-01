package com.krzsta.urlshortener.dto;

public record ShortenRequest (
    String originalUrl,
    Integer daysUntilExpiry
) {}
