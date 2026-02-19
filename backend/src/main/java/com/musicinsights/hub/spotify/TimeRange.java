package com.musicinsights.hub.spotify;

public enum TimeRange {
  short_term,
  medium_term,
  long_term;

  public static TimeRange parse(String value) {
    for (TimeRange candidate : values()) {
      if (candidate.name().equals(value)) {
        return candidate;
      }
    }
    throw new IllegalArgumentException("Invalid timeRange: " + value);
  }
}
