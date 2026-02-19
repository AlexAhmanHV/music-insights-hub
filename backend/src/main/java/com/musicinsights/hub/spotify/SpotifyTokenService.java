package com.musicinsights.hub.spotify;

import com.musicinsights.hub.common.AppException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpotifyTokenService {

  private final SpotifyTokenRepository spotifyTokenRepository;

  public SpotifyTokenService(SpotifyTokenRepository spotifyTokenRepository) {
    this.spotifyTokenRepository = spotifyTokenRepository;
  }

  public SpotifyToken getRequiredToken(UUID spotifyAccountId) {
    return spotifyTokenRepository.findBySpotifyAccountId(spotifyAccountId)
        .orElseThrow(() -> new AppException("TOKEN_NOT_FOUND", "Spotify token not found", HttpStatus.UNAUTHORIZED, java.util.List.of()));
  }

  @Transactional
  public SpotifyToken upsertToken(SpotifyAccount account, SpotifyTokenResponse tokenResponse) {
    SpotifyToken token = spotifyTokenRepository.findBySpotifyAccountId(account.getId()).orElseGet(SpotifyToken::new);
    token.setSpotifyAccount(account);
    token.setAccessToken(tokenResponse.access_token());
    if (tokenResponse.refresh_token() != null && !tokenResponse.refresh_token().isBlank()) {
      token.setRefreshToken(tokenResponse.refresh_token());
    } else if (token.getRefreshToken() == null) {
      token.setRefreshToken("");
    }
    token.setExpiresAt(Instant.now().plusSeconds(tokenResponse.expires_in() == null ? 3600 : tokenResponse.expires_in()));
    token.setScopes(tokenResponse.scope() == null ? "" : tokenResponse.scope());
    return spotifyTokenRepository.save(token);
  }
}
