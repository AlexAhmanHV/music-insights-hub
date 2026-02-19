package com.musicinsights.hub.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "oauth_states")
public class OauthState {

  @Id
  @Column(name = "state")
  private String state;

  @Column(name = "code_verifier", nullable = false, columnDefinition = "text")
  private String codeVerifier;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
  }

  public String getState() { return state; }
  public void setState(String state) { this.state = state; }
  public String getCodeVerifier() { return codeVerifier; }
  public void setCodeVerifier(String codeVerifier) { this.codeVerifier = codeVerifier; }
  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
