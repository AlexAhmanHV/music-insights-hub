package com.musicinsights.hub.playlist;

import com.musicinsights.hub.spotify.PlaylistResult;
import com.musicinsights.hub.spotify.SpotifyGatewayRouter;
import com.musicinsights.hub.spotify.TimeRange;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

  private final SpotifyGatewayRouter spotifyGatewayRouter;

  public PlaylistService(SpotifyGatewayRouter spotifyGatewayRouter) {
    this.spotifyGatewayRouter = spotifyGatewayRouter;
  }

  public CreatePlaylistResponse createTopTracksPlaylist(UUID spotifyAccountId, CreateTopTracksPlaylistRequest request) {
    TimeRange timeRange = TimeRange.parse(request.timeRange());
    var tracks = spotifyGatewayRouter.resolve().getTopTracks(spotifyAccountId, timeRange, request.limit());
    PlaylistResult result = spotifyGatewayRouter.resolve().createPlaylistFromTracks(
        spotifyAccountId,
        request.name(),
        tracks.stream().map(t -> t.uri()).toList());
    return new CreatePlaylistResponse(result.playlistId(), result.playlistUrl());
  }
}
