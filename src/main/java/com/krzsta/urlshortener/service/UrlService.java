package com.krzsta.urlshortener.service;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.krzsta.urlshortener.model.Url;
import com.krzsta.urlshortener.repository.UrlRepository;
import com.krzsta.urlshortener.dto.ShortenRequest;

@Service
public class UrlService {
    
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String shorten(ShortenRequest request) {
        String shortCode;
        do {
            shortCode = UUID.randomUUID().toString().substring(0, 6);
        } while (urlRepository.existsByShortCode(shortCode));
        LocalDateTime expiresAt = request.daysUntilExpiry() != null
            ? LocalDateTime.now().plusDays(request.daysUntilExpiry())
            : null;
        Url url = new Url(request.originalUrl(), shortCode, expiresAt);
        urlRepository.save(url);
        return shortCode;
    }

    public String redirect(String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        if (url.isEmpty()) {
            throw new IllegalArgumentException("Invalid short code");
        }
        
        Url found = url.get();

        if (found.getExpiresAt() != null && found.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Link has expired.");
        }

        found .setClicks(found.getClicks() + 1);
        urlRepository.save(found);

        return url.get().getOriginalUrl();
    }
}
