package com.musicinsights.hub.spotify;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, UUID> {
  Optional<SpotifyToken> findBySpotifyAccountId(UUID spotifyAccountId);
}
