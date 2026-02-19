package com.musicinsights.hub.insights;

public record TrendItem(String id, String name, String itemType, Integer previousRank, Integer currentRank, Integer rankDelta) {}
