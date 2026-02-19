package com.musicinsights.hub.insights;

import com.musicinsights.hub.spotify.SpotifyArtist;
import com.musicinsights.hub.spotify.SpotifyTrack;
import java.util.List;

public record TopInsightsResponse(List<SpotifyArtist> topArtists, List<SpotifyTrack> topTracks, List<GenreCount> genreBreakdown) {}
