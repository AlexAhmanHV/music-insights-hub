package com.musicinsights.hub.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthController {

  private final AuthService authService;
  private final com.musicinsights.hub.config.AppProperties appProperties;

  public AuthController(AuthService authService, com.musicinsights.hub.config.AppProperties appProperties) {
    this.authService = authService;
    this.appProperties = appProperties;
  }

  @GetMapping("/auth/spotify/start")
  public Map<String, String> start(HttpServletResponse response) {
    AuthService.AuthStartResponse startResponse = authService.buildAuthorizeUrl();
    if (startResponse.state() != null) {
      response.addHeader(HttpHeaders.SET_COOKIE, authService.buildOauthStateCookie(startResponse.state()).toString());
    }
    return Map.of("authorizeUrl", startResponse.authorizeUrl());
  }

  @GetMapping("/auth/spotify/callback")
  public void callback(@RequestParam String code, @RequestParam(required = false) String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
    String stateCookie = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
        .filter(cookie -> AuthService.OAUTH_STATE_COOKIE.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
    try {
      authService.handleCallback(code, state, stateCookie, response);
      response.sendRedirect(appProperties.baseUrl() + "/dashboard");
    } finally {
      response.addHeader(HttpHeaders.SET_COOKIE, authService.buildOauthStateCookie(null).toString());
    }
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    authService.logout(response);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public MeResponse me(Authentication authentication) {
    SessionClaims claims = authentication == null ? null : (SessionClaims) authentication.getPrincipal();
    return authService.me(claims);
  }
}
