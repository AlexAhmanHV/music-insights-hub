package com.musicinsights.hub.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TopArtistsApiResponse(List<ArtistItem> items) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ArtistItem(String id, String name, String uri, List<String> genres) {}
}
