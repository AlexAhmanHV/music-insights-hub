package com.musicinsights.hub.unit;

import com.musicinsights.hub.insights.SnapshotRankedItem;
import com.musicinsights.hub.insights.TrendComparisonService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TrendComparisonServiceTest {

  private final TrendComparisonService service = new TrendComparisonService();

  @Test
  void compareShouldDetectNewClimbersAndDroppers() {
    var latest = java.util.List.of(
        new SnapshotRankedItem("track-1", "Track 1", "TRACK", 1),
        new SnapshotRankedItem("track-2", "Track 2", "TRACK", 3),
        new SnapshotRankedItem("track-3", "Track 3", "TRACK", 4));

    var previous = java.util.List.of(
        new SnapshotRankedItem("track-1", "Track 1", "TRACK", 2),
        new SnapshotRankedItem("track-2", "Track 2", "TRACK", 1),
        new SnapshotRankedItem("track-4", "Track 4", "TRACK", 3));

    var result = service.compare(latest, previous);

    Assertions.assertEquals(1, result.newEntries().size());
    Assertions.assertEquals("track-3", result.newEntries().getFirst().id());
    Assertions.assertEquals(1, result.climbers().size());
    Assertions.assertEquals("track-1", result.climbers().getFirst().id());
    Assertions.assertEquals(1, result.droppers().size());
    Assertions.assertEquals("track-2", result.droppers().getFirst().id());
  }
}
