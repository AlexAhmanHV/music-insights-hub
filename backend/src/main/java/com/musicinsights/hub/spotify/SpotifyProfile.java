package com.musicinsights.hub.spotify;

import java.util.List;

public record SpotifyProfile(String id, String displayName, String email, String country) {

  public static SpotifyProfile fromApiResponse(SpotifyProfileApi api) {
    return new SpotifyProfile(api.id(), api.display_name(), api.email(), api.country());
  }

  public record SpotifyProfileApi(String id, String display_name, String email, String country) {}
}
