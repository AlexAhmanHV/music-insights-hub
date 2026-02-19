package com.musicinsights.hub.spotify;

import java.util.List;

public record SpotifyArtist(String id, String name, String uri, List<String> genres, int rank) {}
