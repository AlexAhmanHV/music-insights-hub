package com.musicinsights.hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  WebClient spotifyApiWebClient() {
    return WebClient.builder()
        .baseUrl("https://api.spotify.com")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  WebClient spotifyAuthWebClient() {
    return WebClient.builder()
        .baseUrl("https://accounts.spotify.com")
        .build();
  }
}
