package com.musicinsights.hub.spotify;

import com.musicinsights.hub.user.AppUser;
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
@Table(name = "spotify_accounts")
public class SpotifyAccount {

  @Id
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "app_user_id", nullable = false)
  private AppUser appUser;

  @Column(name = "spotify_user_id", nullable = false, unique = true)
  private String spotifyUserId;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "email")
  private String email;

  @Column(name = "country")
  private String country;

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
  public AppUser getAppUser() { return appUser; }
  public void setAppUser(AppUser appUser) { this.appUser = appUser; }
  public String getSpotifyUserId() { return spotifyUserId; }
  public void setSpotifyUserId(String spotifyUserId) { this.spotifyUserId = spotifyUserId; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }
}
