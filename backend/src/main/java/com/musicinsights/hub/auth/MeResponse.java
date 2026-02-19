package com.musicinsights.hub.auth;

public record MeResponse(boolean connected, String appUserId, SpotifyAccountSummary spotifyAccount, boolean spotifyMockMode) {
  public record SpotifyAccountSummary(String spotifyUserId, String displayName, String email, String country) {}
}
