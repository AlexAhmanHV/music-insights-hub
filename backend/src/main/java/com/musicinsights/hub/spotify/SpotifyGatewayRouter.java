package com.musicinsights.hub.spotify;

import com.musicinsights.hub.config.AppProperties;
import org.springframework.stereotype.Component;

@Component
public class SpotifyGatewayRouter {

  private final AppProperties appProperties;
  private final MockSpotifyGateway mockSpotifyGateway;
  private final RealSpotifyGateway realSpotifyGateway;

  public SpotifyGatewayRouter(AppProperties appProperties, MockSpotifyGateway mockSpotifyGateway, RealSpotifyGateway realSpotifyGateway) {
    this.appProperties = appProperties;
    this.mockSpotifyGateway = mockSpotifyGateway;
    this.realSpotifyGateway = realSpotifyGateway;
  }

  public SpotifyGateway resolve() {
    return appProperties.spotifyMockMode() ? mockSpotifyGateway : realSpotifyGateway;
  }

  public RealSpotifyGateway real() {
    return realSpotifyGateway;
  }
}
