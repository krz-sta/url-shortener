package com.krzsta.urlshortener.service;

import com.krzsta.urlshortener.dto.ShortenRequest;
import com.krzsta.urlshortener.dto.UrlResponse;
import com.krzsta.urlshortener.model.Url;
import com.krzsta.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shorten_shouldSaveUrlAndReturnShortCode() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(i -> i.getArgument(0));

        ShortenRequest request = new ShortenRequest("https://google.com", 7);
        String result = urlService.shorten(request);

        assertNotNull(result);
        assertEquals(6, result.length());
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void shorten_withNullExpiry_shouldSaveWithoutExpiryDate() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(i -> i.getArgument(0));

        ShortenRequest request = new ShortenRequest("https://google.com", null);
        String result = urlService.shorten(request);

        assertNotNull(result);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void redirect_shouldIncrementClicksAndReturnOriginalUrl() {
        Url url = new Url("https://google.com", "abc123", LocalDateTime.now().plusDays(7));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenAnswer(i -> i.getArgument(0));

        String result = urlService.redirect("abc123");

        assertEquals("https://google.com", result);
        assertEquals(1, url.getClicks());
        verify(urlRepository, times(1)).save(url);
    }

    @Test
    void redirect_withInvalidCode_shouldThrowException() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> urlService.redirect("invalid"));
    }

    @Test
    void redirect_withExpiredUrl_shouldThrowException() {
        Url url = new Url("https://google.com", "abc123", LocalDateTime.now().minusDays(1));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        assertThrows(IllegalStateException.class, () -> urlService.redirect("abc123"));
    }

    @Test
    void getStats_shouldReturnUrlResponse() {
        Url url = new Url("https://google.com", "abc123", LocalDateTime.now().plusDays(7));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        UrlResponse response = urlService.getStats("abc123");

        assertEquals("https://google.com", response.originalUrl());
        assertEquals("abc123", response.shortCode());
        assertEquals(0, response.clicks());
    }

    @Test
    void getStats_withInvalidCode_shouldThrowException() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> urlService.getStats("invalid"));
    }
}