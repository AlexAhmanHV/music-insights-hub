package com.musicinsights.hub.playlist;

import com.musicinsights.hub.auth.SessionClaims;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

  private final PlaylistService playlistService;

  public PlaylistController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  @PostMapping("/top-tracks")
  public CreatePlaylistResponse createTopTracks(@Valid @RequestBody CreateTopTracksPlaylistRequest request, Authentication authentication) {
    SessionClaims claims = (SessionClaims) authentication.getPrincipal();
    return playlistService.createTopTracksPlaylist(claims.spotifyAccountId(), request);
  }
}
