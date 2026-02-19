package com.musicinsights.hub.spotify;

import com.musicinsights.hub.config.AppProperties;
import com.musicinsights.hub.common.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class RealSpotifyGateway implements SpotifyGateway {

  private final WebClient spotifyApiWebClient;
  private final WebClient spotifyAuthWebClient;
  private final SpotifyTokenService spotifyTokenService;
  private final AppProperties appProperties;

  public RealSpotifyGateway(
      WebClient spotifyApiWebClient,
      WebClient spotifyAuthWebClient,
      SpotifyTokenService spotifyTokenService,
      AppProperties appProperties,
      ObjectMapper objectMapper) {
    this.spotifyApiWebClient = spotifyApiWebClient;
    this.spotifyAuthWebClient = spotifyAuthWebClient;
    this.spotifyTokenService = spotifyTokenService;
    this.appProperties = appProperties;
  }

  @Override
  public SpotifyProfile getCurrentUser(UUID spotifyAccountId) {
    SpotifyProfile.SpotifyProfileApi api = callGet(spotifyAccountId, "/v1/me", SpotifyProfile.SpotifyProfileApi.class);
    return SpotifyProfile.fromApiResponse(api);
  }

  public SpotifyProfile getCurrentUserWithAccessToken(String accessToken) {
    SpotifyProfile.SpotifyProfileApi api = spotifyApiWebClient.get()
        .uri("/v1/me")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(SpotifyProfile.SpotifyProfileApi.class)
        .block(Duration.ofSeconds(15));
    return SpotifyProfile.fromApiResponse(api);
  }

  @Override
  public List<SpotifyArtist> getTopArtists(UUID spotifyAccountId, TimeRange timeRange, int limit) {
    TopArtistsApiResponse response = callGet(spotifyAccountId,
        "/v1/me/top/artists?time_range=" + timeRange.name() + "&limit=" + limit,
        TopArtistsApiResponse.class);
    List<TopArtistsApiResponse.ArtistItem> items = response.items() == null ? List.of() : response.items();
    return java.util.stream.IntStream.range(0, items.size())
        .mapToObj(idx -> {
          TopArtistsApiResponse.ArtistItem item = items.get(idx);
          return new SpotifyArtist(item.id(), item.name(), item.uri(), item.genres() == null ? List.of() : item.genres(), idx + 1);
        }).toList();
  }

  @Override
  public List<SpotifyTrack> getTopTracks(UUID spotifyAccountId, TimeRange timeRange, int limit) {
    TopTracksApiResponse response = callGet(spotifyAccountId,
        "/v1/me/top/tracks?time_range=" + timeRange.name() + "&limit=" + limit,
        TopTracksApiResponse.class);
    List<TopTracksApiResponse.TrackItem> items = response.items() == null ? List.of() : response.items();
    return java.util.stream.IntStream.range(0, items.size())
        .mapToObj(idx -> {
          TopTracksApiResponse.TrackItem item = items.get(idx);
          String artistName = (item.artists() == null || item.artists().isEmpty()) ? "Unknown" : item.artists().getFirst().name();
          return new SpotifyTrack(item.id(), item.name(), item.uri(), artistName, idx + 1);
        }).toList();
  }

  @Override
  public PlaylistResult createPlaylistFromTracks(UUID spotifyAccountId, String name, List<String> trackUris) {
    SpotifyProfile me = getCurrentUser(spotifyAccountId);

    record CreatePlaylistRequest(String name, String description, boolean _public) {}
    record ExternalUrls(String spotify) {}
    record CreatePlaylistResponse(String id, ExternalUrls external_urls) {}

    CreatePlaylistResponse createResponse = callPost(spotifyAccountId,
        "/v1/users/" + me.id() + "/playlists",
        new CreatePlaylistRequest(name, "Created by Music Insights Hub", false),
        CreatePlaylistResponse.class);

    record AddItemsRequest(List<String> uris) {}
    callPost(spotifyAccountId, "/v1/playlists/" + createResponse.id() + "/tracks", new AddItemsRequest(trackUris), new ParameterizedTypeReference<java.util.Map<String, Object>>() {});

    String playlistUrl = createResponse.external_urls() == null ? "" : createResponse.external_urls().spotify();
    return new PlaylistResult(createResponse.id(), playlistUrl);
  }

  public SpotifyTokenResponse exchangeCode(String code, String codeVerifier) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("code", code);
    body.add("redirect_uri", appProperties.spotifyRedirectUri());
    body.add("client_id", appProperties.spotifyClientId());
    body.add("code_verifier", codeVerifier);
    if (appProperties.spotifyClientSecret() != null && !appProperties.spotifyClientSecret().isBlank()) {
      body.add("client_secret", appProperties.spotifyClientSecret());
    }

    return spotifyAuthWebClient.post()
        .uri("/api/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block(Duration.ofSeconds(10));
  }

  public SpotifyTokenResponse refreshToken(String refreshToken) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "refresh_token");
    body.add("refresh_token", refreshToken);
    body.add("client_id", appProperties.spotifyClientId());
    if (appProperties.spotifyClientSecret() != null && !appProperties.spotifyClientSecret().isBlank()) {
      body.add("client_secret", appProperties.spotifyClientSecret());
    }
    return spotifyAuthWebClient.post()
        .uri("/api/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block(Duration.ofSeconds(10));
  }

  private <T> T callGet(UUID spotifyAccountId, String uri, Class<T> type) {
    return withRetry(spotifyAccountId, token -> spotifyApiWebClient.get()
        .uri(uri)
        .headers(h -> h.setBearerAuth(token))
        .retrieve()
        .bodyToMono(type)
        .block(Duration.ofSeconds(15)));
  }

  private <T> T callPost(UUID spotifyAccountId, String uri, Object body, Class<T> type) {
    return withRetry(spotifyAccountId, token -> spotifyApiWebClient.post()
        .uri(uri)
        .headers(h -> h.setBearerAuth(token))
        .bodyValue(body)
        .retrieve()
        .bodyToMono(type)
        .block(Duration.ofSeconds(15)));
  }

  private <T> T callPost(UUID spotifyAccountId, String uri, Object body, ParameterizedTypeReference<T> type) {
    return withRetry(spotifyAccountId, token -> spotifyApiWebClient.post()
        .uri(uri)
        .headers(h -> h.setBearerAuth(token))
        .bodyValue(body)
        .retrieve()
        .bodyToMono(type)
        .block(Duration.ofSeconds(15)));
  }

  private <T> T withRetry(UUID spotifyAccountId, java.util.function.Function<String, T> call) {
    SpotifyToken token = spotifyTokenService.getRequiredToken(spotifyAccountId);
    try {
      return call.apply(token.getAccessToken());
    } catch (WebClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
        String retryAfter = ex.getHeaders().getFirst("Retry-After");
        long sleepSeconds = retryAfter == null ? 1 : Long.parseLong(retryAfter);
        sleepUnchecked(sleepSeconds * 1000L);
        return call.apply(token.getAccessToken());
      }
      if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        SpotifyTokenResponse refreshed = refreshToken(token.getRefreshToken());
        spotifyTokenService.upsertToken(token.getSpotifyAccount(), refreshed);
        return call.apply(refreshed.access_token());
      }
      throw new AppException("SPOTIFY_ERROR", "Spotify API call failed", HttpStatus.BAD_GATEWAY, java.util.List.of(ex.getMessage()));
    }
  }

  private void sleepUnchecked(long millis) {
    try {
      Thread.sleep(Math.min(millis, 3000));
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
