package com.musicinsights.hub.auth;

import com.musicinsights.hub.config.AppProperties;
import com.musicinsights.hub.common.AppException;
import com.musicinsights.hub.spotify.SpotifyAccount;
import com.musicinsights.hub.spotify.SpotifyAccountRepository;
import com.musicinsights.hub.spotify.SpotifyProfile;
import com.musicinsights.hub.spotify.SpotifyGatewayRouter;
import com.musicinsights.hub.spotify.SpotifyTokenService;
import com.musicinsights.hub.spotify.SpotifyTokenResponse;
import com.musicinsights.hub.user.AppUser;
import com.musicinsights.hub.user.AppUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);
  public static final String OAUTH_STATE_COOKIE = "oauth_state";
  private static final int OAUTH_STATE_TTL_SECONDS = 600;
  private static final int SESSION_TTL_SECONDS = 86400;
  private static final String STATE_PATTERN = "^[A-Za-z0-9_-]{20,200}$";
  private static final String SCOPES = "user-top-read playlist-modify-public playlist-modify-private user-read-email";

  private final AppProperties appProperties;
  private final OauthStateRepository oauthStateRepository;
  private final OauthStateService oauthStateService;
  private final SpotifyGatewayRouter spotifyGatewayRouter;
  private final SpotifyAccountRepository spotifyAccountRepository;
  private final SpotifyTokenService spotifyTokenService;
  private final AppUserRepository appUserRepository;
  private final JwtService jwtService;

  public AuthService(
      AppProperties appProperties,
      OauthStateRepository oauthStateRepository,
      OauthStateService oauthStateService,
      SpotifyGatewayRouter spotifyGatewayRouter,
      SpotifyAccountRepository spotifyAccountRepository,
      SpotifyTokenService spotifyTokenService,
      AppUserRepository appUserRepository,
      JwtService jwtService) {
    this.appProperties = appProperties;
    this.oauthStateRepository = oauthStateRepository;
    this.oauthStateService = oauthStateService;
    this.spotifyGatewayRouter = spotifyGatewayRouter;
    this.spotifyAccountRepository = spotifyAccountRepository;
    this.spotifyTokenService = spotifyTokenService;
    this.appUserRepository = appUserRepository;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthStartResponse buildAuthorizeUrl() {
    oauthStateRepository.deleteByExpiresAtBefore(Instant.now());

    if (appProperties.spotifyMockMode()) {
      return new AuthStartResponse(appProperties.spotifyRedirectUri() + "?code=mock-code&state=mock-state", null);
    }

    String state = randomUrlSafe(32);
    String verifier = randomUrlSafe(64);
    String challenge = sha256Url(verifier);

    OauthState oauthState = new OauthState();
    oauthState.setState(state);
    oauthState.setCodeVerifier(verifier);
    oauthState.setExpiresAt(Instant.now().plusSeconds(OAUTH_STATE_TTL_SECONDS));
    oauthStateRepository.save(oauthState);

    String authorizeUrl = "https://accounts.spotify.com/authorize"
        + "?response_type=code"
        + "&client_id=" + encode(appProperties.spotifyClientId())
        + "&scope=" + encode(SCOPES)
        + "&redirect_uri=" + encode(appProperties.spotifyRedirectUri())
        + "&state=" + encode(state)
        + "&code_challenge_method=S256"
        + "&code_challenge=" + encode(challenge);

    return new AuthStartResponse(authorizeUrl, state);
  }

  @Transactional
  public void handleCallback(String code, String state, String stateCookie, HttpServletResponse response) {
    SpotifyAccount account;

    if (appProperties.spotifyMockMode()) {
      account = ensureMockAccount();
    } else {
      if (code == null || code.isBlank()) {
        throw new AppException("INVALID_CODE", "OAuth code is required", HttpStatus.BAD_REQUEST, java.util.List.of());
      }
      if (state == null || state.isBlank() || !state.matches(STATE_PATTERN)) {
        throw new AppException("INVALID_STATE", "OAuth state is missing or malformed", HttpStatus.BAD_REQUEST, java.util.List.of());
      }
      boolean localRedirect = isLocalRedirectUri();
      if (stateCookie == null || stateCookie.isBlank()) {
        if (!localRedirect) {
          throw new AppException("INVALID_STATE", "OAuth state mismatch", HttpStatus.BAD_REQUEST, java.util.List.of());
        }
        log.warn("OAuth state cookie missing in local mode; falling back to DB state validation only");
      } else if (!state.equals(stateCookie)) {
        if (!localRedirect) {
          throw new AppException("INVALID_STATE", "OAuth state mismatch", HttpStatus.BAD_REQUEST, java.util.List.of());
        }
        log.warn("OAuth state cookie mismatch in local mode; falling back to DB state validation only");
      }
      String codeVerifier = oauthStateService.consumeCodeVerifier(state);
      SpotifyTokenResponse tokenResponse = spotifyGatewayRouter.real().exchangeCode(code, codeVerifier);
      if (tokenResponse == null || tokenResponse.access_token() == null || tokenResponse.access_token().isBlank()) {
        throw new AppException("TOKEN_EXCHANGE_FAILED", "Spotify token exchange failed", HttpStatus.BAD_GATEWAY, java.util.List.of());
      }
      SpotifyProfile profile = spotifyGatewayRouter.real().getCurrentUserWithAccessToken(tokenResponse.access_token());
      account = upsertAccount(profile);
      spotifyTokenService.upsertToken(account, tokenResponse);
    }

    String jwt = jwtService.createToken(new SessionClaims(account.getAppUser().getId(), account.getId()), Instant.now().plusSeconds(SESSION_TTL_SECONDS));
    response.addHeader(HttpHeaders.SET_COOKIE, buildSessionCookie(jwt, SESSION_TTL_SECONDS).toString());
  }

  @Transactional
  public void logout(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, buildSessionCookie("", 0).toString());
  }

  public ResponseCookie buildOauthStateCookie(String state) {
    return ResponseCookie.from(OAUTH_STATE_COOKIE, state == null ? "" : state)
        .httpOnly(true)
        .secure(appProperties.sessionCookieSecure())
        .path("/auth/spotify/callback")
        .sameSite(resolveSameSite())
        .maxAge(state == null ? 0 : OAUTH_STATE_TTL_SECONDS)
        .build();
  }

  private ResponseCookie buildSessionCookie(String tokenValue, long maxAgeSeconds) {
    return ResponseCookie.from("session", tokenValue)
        .httpOnly(true)
        .secure(appProperties.sessionCookieSecure())
        .path("/")
        .sameSite(resolveSameSite())
        .maxAge(maxAgeSeconds)
        .build();
  }

  public MeResponse me(SessionClaims claims) {
    if (claims == null) {
      return new MeResponse(false, null, null, appProperties.spotifyMockMode());
    }

    SpotifyAccount account = spotifyAccountRepository.findById(claims.spotifyAccountId())
        .orElseThrow(() -> new AppException("ACCOUNT_NOT_FOUND", "Spotify account not found", HttpStatus.NOT_FOUND, java.util.List.of()));

    return new MeResponse(
        true,
        claims.appUserId().toString(),
        new MeResponse.SpotifyAccountSummary(
            account.getSpotifyUserId(),
            account.getDisplayName(),
            account.getEmail(),
            account.getCountry()),
        appProperties.spotifyMockMode());
  }

  private SpotifyAccount ensureMockAccount() {
    SpotifyProfile profile = spotifyGatewayRouter.resolve().getCurrentUser(UUID.randomUUID());
    return upsertAccount(profile);
  }

  private SpotifyAccount upsertAccount(SpotifyProfile profile) {
    return spotifyAccountRepository.findBySpotifyUserId(profile.id())
        .map(existing -> {
          existing.setDisplayName(profile.displayName());
          existing.setEmail(profile.email());
          existing.setCountry(profile.country());
          return spotifyAccountRepository.save(existing);
        })
        .orElseGet(() -> {
          AppUser appUser = appUserRepository.save(new AppUser());
          SpotifyAccount account = new SpotifyAccount();
          account.setAppUser(appUser);
          account.setSpotifyUserId(profile.id());
          account.setDisplayName(profile.displayName());
          account.setEmail(profile.email());
          account.setCountry(profile.country());
          return spotifyAccountRepository.save(account);
        });
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static String randomUrlSafe(int bytes) {
    byte[] random = new byte[bytes];
    new SecureRandom().nextBytes(random);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
  }

  private static String sha256Url(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  private boolean isLocalRedirectUri() {
    String uri = appProperties.spotifyRedirectUri();
    return uri != null && (uri.startsWith("http://localhost:") || uri.startsWith("http://127.0.0.1:"));
  }

  private String resolveSameSite() {
    return appProperties.sessionCookieSecure() ? "None" : "Lax";
  }

  public record AuthStartResponse(String authorizeUrl, String state) {}
}
