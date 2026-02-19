package com.musicinsights.hub.insights;

import java.util.List;

public record TrendsResponse(List<TrendItem> newEntries, List<TrendItem> climbers, List<TrendItem> droppers) {}
