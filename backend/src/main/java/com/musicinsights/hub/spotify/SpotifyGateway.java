package com.musicinsights.hub.spotify;

import java.util.List;
import java.util.UUID;

public interface SpotifyGateway {
  SpotifyProfile getCurrentUser(UUID spotifyAccountId);
  List<SpotifyArtist> getTopArtists(UUID spotifyAccountId, TimeRange timeRange, int limit);
  List<SpotifyTrack> getTopTracks(UUID spotifyAccountId, TimeRange timeRange, int limit);
  PlaylistResult createPlaylistFromTracks(UUID spotifyAccountId, String name, List<String> trackUris);
}
