package com.musicinsights.hub.insights;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TrendComparisonService {

  public TrendsResponse compare(List<SnapshotRankedItem> latest, List<SnapshotRankedItem> previous) {
    Map<String, SnapshotRankedItem> previousMap = previous.stream().collect(Collectors.toMap(
        item -> key(item.itemType(), item.id()),
        item -> item,
        (a, b) -> a));

    List<TrendItem> newEntries = latest.stream()
        .filter(item -> !previousMap.containsKey(key(item.itemType(), item.id())))
        .map(item -> new TrendItem(item.id(), item.name(), item.itemType(), null, item.rank(), null))
        .toList();

    List<TrendItem> climbers = latest.stream()
        .map(item -> {
          SnapshotRankedItem previousItem = previousMap.get(key(item.itemType(), item.id()));
          if (previousItem == null) {
            return null;
          }
          int delta = previousItem.rank() - item.rank();
          if (delta > 0) {
            return new TrendItem(item.id(), item.name(), item.itemType(), previousItem.rank(), item.rank(), delta);
          }
          return null;
        })
        .filter(java.util.Objects::nonNull)
        .sorted(Comparator.comparing(TrendItem::rankDelta).reversed())
        .toList();

    List<TrendItem> droppers = latest.stream()
        .map(item -> {
          SnapshotRankedItem previousItem = previousMap.get(key(item.itemType(), item.id()));
          if (previousItem == null) {
            return null;
          }
          int delta = previousItem.rank() - item.rank();
          if (delta < 0) {
            return new TrendItem(item.id(), item.name(), item.itemType(), previousItem.rank(), item.rank(), delta);
          }
          return null;
        })
        .filter(java.util.Objects::nonNull)
        .sorted(Comparator.comparing(TrendItem::rankDelta))
        .toList();

    return new TrendsResponse(newEntries, climbers, droppers);
  }

  private String key(String type, String id) {
    return type + "::" + id;
  }
}
