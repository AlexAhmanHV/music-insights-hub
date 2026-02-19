package com.musicinsights.hub.unit;

import com.musicinsights.hub.auth.OauthState;
import com.musicinsights.hub.auth.OauthStateRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OauthStateExpiryTest {

  @Test
  void shouldDeleteExpiredStates() {
    OauthStateRepository repository = Mockito.mock(OauthStateRepository.class);
    repository.deleteByExpiresAtBefore(Instant.now());
    Mockito.verify(repository, Mockito.times(1)).deleteByExpiresAtBefore(Mockito.any(Instant.class));
  }
}
