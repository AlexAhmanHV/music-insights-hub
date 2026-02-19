package com.musicinsights.hub.spotify;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyAccountRepository extends JpaRepository<SpotifyAccount, UUID> {
  Optional<SpotifyAccount> findBySpotifyUserId(String spotifyUserId);
  Optional<SpotifyAccount> findByAppUserId(UUID appUserId);
}
