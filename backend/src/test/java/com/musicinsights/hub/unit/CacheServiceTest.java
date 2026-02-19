package com.musicinsights.hub.unit;

import com.musicinsights.hub.config.AppProperties;
import com.musicinsights.hub.spotify.CacheEntry;
import com.musicinsights.hub.spotify.CacheEntryRepository;
import com.musicinsights.hub.spotify.CacheService;
import com.musicinsights.hub.spotify.SpotifyAccount;
import com.musicinsights.hub.spotify.SpotifyAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CacheServiceTest {

  @Test
  void getShouldIgnoreExpiredCacheEntries() {
    var cacheRepo = Mockito.mock(CacheEntryRepository.class);
    var accountRepo = Mockito.mock(SpotifyAccountRepository.class);
    var props = new AppProperties("http://localhost:5173", "secret", true, "", "", "", 20, false);
    var service = new CacheService(cacheRepo, accountRepo, props);

    UUID accountId = UUID.randomUUID();
    CacheEntry entry = new CacheEntry();
    SpotifyAccount account = new SpotifyAccount();
    entry.setSpotifyAccount(account);
    entry.setCacheKey("top::medium_term");
    entry.setPayload(new ObjectMapper().valueToTree(java.util.Map.of("hello", "world")));
    entry.setExpiresAt(Instant.now().minusSeconds(10));

    Mockito.when(cacheRepo.findBySpotifyAccountIdAndCacheKey(accountId, "top::medium_term")).thenReturn(Optional.of(entry));

    Assertions.assertTrue(service.get(accountId, "top::medium_term").isEmpty());
  }
}
