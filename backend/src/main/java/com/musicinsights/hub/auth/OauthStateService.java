package com.musicinsights.hub.auth;

import com.musicinsights.hub.common.AppException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OauthStateService {

  private final OauthStateRepository oauthStateRepository;

  public OauthStateService(OauthStateRepository oauthStateRepository) {
    this.oauthStateRepository = oauthStateRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public String consumeCodeVerifier(String state) {
    OauthState oauthState = oauthStateRepository.findById(state)
        .orElseThrow(() -> new AppException("INVALID_STATE", "OAuth state not found", HttpStatus.BAD_REQUEST, List.of()));
    if (oauthState.getExpiresAt().isBefore(Instant.now())) {
      oauthStateRepository.deleteById(state);
      throw new AppException("EXPIRED_STATE", "OAuth state expired", HttpStatus.BAD_REQUEST, List.of());
    }
    oauthStateRepository.deleteById(state);
    return oauthState.getCodeVerifier();
  }
}
