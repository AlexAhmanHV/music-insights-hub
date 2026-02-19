package com.musicinsights.hub.spotify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MockSpotifyGateway implements SpotifyGateway {

  private final SpotifyProfile profile;
  private final List<SpotifyArtist> artists;
  private final List<SpotifyTrack> tracks;

  public MockSpotifyGateway(ObjectMapper objectMapper) {
    try {
      this.profile = load(objectMapper, "fixtures/mock-me.json", SpotifyProfile.class);
      this.artists = load(objectMapper, "fixtures/mock-top-artists.json", new TypeReference<>() {});
      this.tracks = load(objectMapper, "fixtures/mock-top-tracks.json", new TypeReference<>() {});
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to load mock fixtures", ex);
    }
  }

  @Override
  public SpotifyProfile getCurrentUser(UUID spotifyAccountId) {
    return profile;
  }

  @Override
  public List<SpotifyArtist> getTopArtists(UUID spotifyAccountId, TimeRange timeRange, int limit) {
    return artists.stream().limit(limit).toList();
  }

  @Override
  public List<SpotifyTrack> getTopTracks(UUID spotifyAccountId, TimeRange timeRange, int limit) {
    return tracks.stream().limit(limit).toList();
  }

  @Override
  public PlaylistResult createPlaylistFromTracks(UUID spotifyAccountId, String name, List<String> trackUris) {
    return new PlaylistResult("mock-playlist-id", "https://open.spotify.com/playlist/mock-playlist-id");
  }

  private <T> T load(ObjectMapper objectMapper, String path, Class<T> type) throws Exception {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      return objectMapper.readValue(input, type);
    }
  }

  private <T> T load(ObjectMapper objectMapper, String path, TypeReference<T> type) throws Exception {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      return objectMapper.readValue(input, type);
    }
  }
}
