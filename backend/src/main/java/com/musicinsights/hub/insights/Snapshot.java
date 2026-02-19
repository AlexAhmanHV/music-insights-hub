package com.musicinsights.hub.insights;

import com.fasterxml.jackson.databind.JsonNode;
import com.musicinsights.hub.spotify.SpotifyAccount;
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
@Table(name = "snapshots")
public class Snapshot {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spotify_account_id", nullable = false)
  private SpotifyAccount spotifyAccount;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "captured_at", nullable = false)
  private Instant capturedAt;

  @Column(name = "time_range", nullable = false)
  private String timeRange;

  @Column(name = "top_artists", nullable = false, columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode topArtists;

  @Column(name = "top_tracks", nullable = false, columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode topTracks;

  @Column(name = "genre_breakdown", nullable = false, columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode genreBreakdown;

  @PrePersist
  void prePersist() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    if (capturedAt == null) {
      capturedAt = Instant.now();
    }
  }

  public UUID getId() { return id; }
  public SpotifyAccount getSpotifyAccount() { return spotifyAccount; }
  public void setSpotifyAccount(SpotifyAccount spotifyAccount) { this.spotifyAccount = spotifyAccount; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Instant getCapturedAt() { return capturedAt; }
  public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }
  public String getTimeRange() { return timeRange; }
  public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
  public JsonNode getTopArtists() { return topArtists; }
  public void setTopArtists(JsonNode topArtists) { this.topArtists = topArtists; }
  public JsonNode getTopTracks() { return topTracks; }
  public void setTopTracks(JsonNode topTracks) { this.topTracks = topTracks; }
  public JsonNode getGenreBreakdown() { return genreBreakdown; }
  public void setGenreBreakdown(JsonNode genreBreakdown) { this.genreBreakdown = genreBreakdown; }
}
