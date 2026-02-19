package com.musicinsights.hub.insights;

import java.time.Instant;
import java.util.UUID;

public record SnapshotSummary(UUID id, String type, String timeRange, Instant capturedAt) {}
