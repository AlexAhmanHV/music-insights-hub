package com.musicinsights.hub.spotify;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "cache_entries")
public class CacheEntry {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spotify_account_id", nullable = false)
  private SpotifyAccount spotifyAccount;

  @Column(name = "cache_key", nullable = false)
  private String cacheKey;

  @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode payload;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @PrePersist
  void prePersist() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  public UUID getId() { return id; }
  public SpotifyAccount getSpotifyAccount() { return spotifyAccount; }
  public void setSpotifyAccount(SpotifyAccount spotifyAccount) { this.spotifyAccount = spotifyAccount; }
  public String getCacheKey() { return cacheKey; }
  public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
  public JsonNode getPayload() { return payload; }
  public void setPayload(JsonNode payload) { this.payload = payload; }
  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
