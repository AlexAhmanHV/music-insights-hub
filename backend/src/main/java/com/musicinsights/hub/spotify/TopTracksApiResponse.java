package com.musicinsights.hub.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TopTracksApiResponse(List<TrackItem> items) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record TrackItem(String id, String name, String uri, List<ArtistRef> artists) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ArtistRef(String name) {}
}
