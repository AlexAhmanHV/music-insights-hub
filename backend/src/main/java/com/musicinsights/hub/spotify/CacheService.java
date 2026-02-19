package com.musicinsights.hub.spotify;

import com.musicinsights.hub.config.AppProperties;
import com.musicinsights.hub.spotify.SpotifyAccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CacheService {

  private final CacheEntryRepository cacheEntryRepository;
  private final SpotifyAccountRepository spotifyAccountRepository;
  private final AppProperties appProperties;

  public CacheService(CacheEntryRepository cacheEntryRepository, SpotifyAccountRepository spotifyAccountRepository, AppProperties appProperties) {
    this.cacheEntryRepository = cacheEntryRepository;
    this.spotifyAccountRepository = spotifyAccountRepository;
    this.appProperties = appProperties;
  }

  public Optional<JsonNode> get(UUID spotifyAccountId, String key) {
    return cacheEntryRepository.findBySpotifyAccountIdAndCacheKey(spotifyAccountId, key)
        .filter(entry -> entry.getExpiresAt().isAfter(Instant.now()))
        .map(CacheEntry::getPayload);
  }

  @Transactional
  public void put(UUID spotifyAccountId, String key, JsonNode payload) {
    SpotifyAccount account = spotifyAccountRepository.findById(spotifyAccountId).orElseThrow();
    CacheEntry entry = cacheEntryRepository.findBySpotifyAccountIdAndCacheKey(spotifyAccountId, key).orElseGet(CacheEntry::new);
    entry.setSpotifyAccount(account);
    entry.setCacheKey(key);
    entry.setPayload(payload);
    entry.setExpiresAt(Instant.now().plusSeconds(appProperties.cacheTtlMinutes() * 60L));
    cacheEntryRepository.save(entry);
  }

  @Transactional
  public void purgeExpired() {
    cacheEntryRepository.deleteByExpiresAtBefore(Instant.now());
  }
}
