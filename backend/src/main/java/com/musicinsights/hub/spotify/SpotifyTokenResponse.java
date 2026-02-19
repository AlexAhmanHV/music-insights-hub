package com.musicinsights.hub.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTokenResponse(String access_token, String token_type, Long expires_in, String refresh_token, String scope) {}
