package com.musicinsights.hub.playlist;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateTopTracksPlaylistRequest(
    @NotBlank String timeRange,
    @Min(1) @Max(50) int limit,
    @NotBlank String name) {}
