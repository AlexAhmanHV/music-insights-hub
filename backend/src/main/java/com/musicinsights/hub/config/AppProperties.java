package com.musicinsights.hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
    String baseUrl,
    String jwtSecret,
    boolean spotifyMockMode,
    String spotifyClientId,
    String spotifyClientSecret,
    String spotifyRedirectUri,
    int cacheTtlMinutes,
    boolean sessionCookieSecure) {}
