package com.musicinsights.hub.auth;

import com.musicinsights.hub.common.AppException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String HMAC_SHA256 = "HmacSHA256";
  private final ObjectMapper objectMapper;
  private final SecretKeySpec secret;

  public JwtService(ObjectMapper objectMapper, com.musicinsights.hub.config.AppProperties appProperties) {
    this.objectMapper = objectMapper;
    this.secret = new SecretKeySpec(appProperties.jwtSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
  }

  public String createToken(SessionClaims claims, Instant expiresAt) {
    try {
      String header = base64Url(objectMapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
      String payload = base64Url(objectMapper.writeValueAsBytes(Map.of(
          "appUserId", claims.appUserId().toString(),
          "spotifyAccountId", claims.spotifyAccountId().toString(),
          "exp", expiresAt.getEpochSecond())));
      String signature = sign(header + "." + payload);
      return header + "." + payload + "." + signature;
    } catch (Exception ex) {
      throw new AppException("JWT_CREATE_FAILED", "Failed to create session token", HttpStatus.INTERNAL_SERVER_ERROR, java.util.List.of(ex.getMessage()));
    }
  }

  public SessionClaims parseToken(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        throw unauthorized("Invalid token format");
      }
      String expectedSignature = sign(parts[0] + "." + parts[1]);
      if (!expectedSignature.equals(parts[2])) {
        throw unauthorized("Invalid token signature");
      }
      Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {});
      long exp = ((Number) payload.get("exp")).longValue();
      if (Instant.now().isAfter(Instant.ofEpochSecond(exp))) {
        throw unauthorized("Session expired");
      }
      UUID appUserId = UUID.fromString((String) payload.get("appUserId"));
      UUID spotifyAccountId = UUID.fromString((String) payload.get("spotifyAccountId"));
      return new SessionClaims(appUserId, spotifyAccountId);
    } catch (AppException ex) {
      throw ex;
    } catch (Exception ex) {
      throw unauthorized("Invalid session token");
    }
  }

  private AppException unauthorized(String message) {
    return new AppException("UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED, java.util.List.of());
  }

  private String sign(String content) throws Exception {
    Mac mac = Mac.getInstance(HMAC_SHA256);
    mac.init(secret);
    return base64Url(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
  }

  private String base64Url(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
