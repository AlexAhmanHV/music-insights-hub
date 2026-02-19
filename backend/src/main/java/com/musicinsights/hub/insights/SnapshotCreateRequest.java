package com.musicinsights.hub.insights;

import jakarta.validation.constraints.NotBlank;

public record SnapshotCreateRequest(@NotBlank String timeRange) {}
