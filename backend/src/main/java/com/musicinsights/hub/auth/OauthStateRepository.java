package com.musicinsights.hub.auth;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthStateRepository extends JpaRepository<OauthState, String> {
  void deleteByExpiresAtBefore(Instant instant);
}
