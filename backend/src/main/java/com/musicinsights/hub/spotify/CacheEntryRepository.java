package com.musicinsights.hub.spotify;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheEntryRepository extends JpaRepository<CacheEntry, UUID> {
  Optional<CacheEntry> findBySpotifyAccountIdAndCacheKey(UUID spotifyAccountId, String cacheKey);
  void deleteByExpiresAtBefore(Instant instant);
}
