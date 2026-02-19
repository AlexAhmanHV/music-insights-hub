package com.musicinsights.hub.insights;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
  List<Snapshot> findBySpotifyAccountIdOrderByCapturedAtDesc(UUID spotifyAccountId, Pageable pageable);
  Optional<Snapshot> findFirstBySpotifyAccountIdAndCapturedAtBeforeOrderByCapturedAtDesc(UUID spotifyAccountId, Instant capturedAt);
}
