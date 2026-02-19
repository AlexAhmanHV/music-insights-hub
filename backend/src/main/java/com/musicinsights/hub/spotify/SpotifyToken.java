package com.musicinsights.hub.spotify;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "spotify_tokens")
public class SpotifyToken {

  @Id
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spotify_account_id", nullable = false)
  private SpotifyAccount spotifyAccount;

  @Column(name = "access_token", nullable = false, columnDefinition = "text")
  private String accessToken;

  @Column(name = "refresh_token", nullable = false, columnDefinition = "text")
  private String refreshToken;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "scopes", nullable = false, columnDefinition = "text")
  private String scopes;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }

  public UUID getId() { return id; }
  public SpotifyAccount getSpotifyAccount() { return spotifyAccount; }
  public void setSpotifyAccount(SpotifyAccount spotifyAccount) { this.spotifyAccount = spotifyAccount; }
  public String getAccessToken() { return accessToken; }
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
  public String getRefreshToken() { return refreshToken; }
  public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
  public String getScopes() { return scopes; }
  public void setScopes(String scopes) { this.scopes = scopes; }
}
