package com.musicinsights.hub.insights;

import com.musicinsights.hub.common.AppException;
import com.musicinsights.hub.spotify.CacheService;
import com.musicinsights.hub.spotify.SpotifyArtist;
import com.musicinsights.hub.spotify.SpotifyGateway;
import com.musicinsights.hub.spotify.SpotifyGatewayRouter;
import com.musicinsights.hub.spotify.SpotifyTrack;
import com.musicinsights.hub.spotify.TimeRange;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsightsService {

  private final SpotifyGatewayRouter spotifyGatewayRouter;
  private final CacheService cacheService;
  private final SnapshotRepository snapshotRepository;
  private final TrendComparisonService trendComparisonService;
  private final com.musicinsights.hub.spotify.SpotifyAccountRepository spotifyAccountRepository;
  private final ObjectMapper objectMapper;

  public InsightsService(
      SpotifyGatewayRouter spotifyGatewayRouter,
      CacheService cacheService,
      SnapshotRepository snapshotRepository,
      TrendComparisonService trendComparisonService,
      com.musicinsights.hub.spotify.SpotifyAccountRepository spotifyAccountRepository,
      ObjectMapper objectMapper) {
    this.spotifyGatewayRouter = spotifyGatewayRouter;
    this.cacheService = cacheService;
    this.snapshotRepository = snapshotRepository;
    this.trendComparisonService = trendComparisonService;
    this.spotifyAccountRepository = spotifyAccountRepository;
    this.objectMapper = objectMapper;
  }

  public TopInsightsResponse getTopInsights(UUID spotifyAccountId, String timeRangeValue) {
    TimeRange timeRange = parseTimeRange(timeRangeValue);
    String cacheKey = "top::" + timeRange.name();
    return cacheService.get(spotifyAccountId, cacheKey)
        .map(this::fromCache)
        .orElseGet(() -> fetchAndCache(spotifyAccountId, timeRange, cacheKey));
  }

  @Transactional
  public SnapshotSummary createSnapshot(UUID spotifyAccountId, String timeRangeValue) {
    TopInsightsResponse top = getTopInsights(spotifyAccountId, timeRangeValue);
    Snapshot snapshot = new Snapshot();
    snapshot.setSpotifyAccount(spotifyAccountRepository.findById(spotifyAccountId).orElseThrow());
    snapshot.setType("MANUAL");
    snapshot.setTimeRange(timeRangeValue);
    snapshot.setCapturedAt(Instant.now());
    snapshot.setTopArtists(write(top.topArtists()));
    snapshot.setTopTracks(write(top.topTracks()));
    snapshot.setGenreBreakdown(write(top.genreBreakdown()));
    Snapshot saved = snapshotRepository.save(snapshot);
    return new SnapshotSummary(saved.getId(), saved.getType(), saved.getTimeRange(), saved.getCapturedAt());
  }

  public List<SnapshotSummary> listSnapshots(UUID spotifyAccountId, int page, int size) {
    return snapshotRepository.findBySpotifyAccountIdOrderByCapturedAtDesc(spotifyAccountId, PageRequest.of(page, size))
        .stream()
        .map(s -> new SnapshotSummary(s.getId(), s.getType(), s.getTimeRange(), s.getCapturedAt()))
        .toList();
  }

  public TrendsResponse compareSnapshots(UUID spotifyAccountId, UUID latestSnapshotId) {
    Snapshot latest = snapshotRepository.findById(latestSnapshotId)
        .orElseThrow(() -> new AppException("SNAPSHOT_NOT_FOUND", "Latest snapshot not found", HttpStatus.NOT_FOUND, List.of()));
    if (!latest.getSpotifyAccount().getId().equals(spotifyAccountId)) {
      throw new AppException("FORBIDDEN", "Snapshot does not belong to current account", HttpStatus.FORBIDDEN, List.of());
    }

    Snapshot previous = snapshotRepository
        .findFirstBySpotifyAccountIdAndCapturedAtBeforeOrderByCapturedAtDesc(spotifyAccountId, latest.getCapturedAt())
        .orElseThrow(() -> new AppException("SNAPSHOT_NOT_FOUND", "Previous snapshot not found", HttpStatus.NOT_FOUND, List.of()));

    List<SnapshotRankedItem> latestItems = new ArrayList<>();
    latestItems.addAll(parseArtists(latest.getTopArtists()));
    latestItems.addAll(parseTracks(latest.getTopTracks()));

    List<SnapshotRankedItem> previousItems = new ArrayList<>();
    previousItems.addAll(parseArtists(previous.getTopArtists()));
    previousItems.addAll(parseTracks(previous.getTopTracks()));

    return trendComparisonService.compare(latestItems, previousItems);
  }

  private TopInsightsResponse fetchAndCache(UUID spotifyAccountId, TimeRange timeRange, String cacheKey) {
    SpotifyGateway gateway = spotifyGatewayRouter.resolve();
    List<SpotifyArtist> artists = gateway.getTopArtists(spotifyAccountId, timeRange, 20);
    List<SpotifyTrack> tracks = gateway.getTopTracks(spotifyAccountId, timeRange, 20);

    List<GenreCount> breakdown = List.of();

    TopInsightsResponse response = new TopInsightsResponse(artists, tracks, breakdown);
    cacheService.put(spotifyAccountId, cacheKey, write(response));
    return response;
  }

  private TopInsightsResponse fromCache(JsonNode payload) {
    try {
      return objectMapper.treeToValue(payload, TopInsightsResponse.class);
    } catch (Exception ex) {
      throw new AppException("CACHE_READ_FAILED", "Failed to read cache entry", HttpStatus.INTERNAL_SERVER_ERROR, List.of(ex.getMessage()));
    }
  }

  private JsonNode write(Object value) {
    try {
      return objectMapper.valueToTree(value);
    } catch (Exception ex) {
      throw new AppException("SERIALIZATION_FAILED", "Failed to serialize payload", HttpStatus.INTERNAL_SERVER_ERROR, List.of(ex.getMessage()));
    }
  }

  private List<SnapshotRankedItem> parseArtists(JsonNode payload) {
    try {
      List<SpotifyArtist> artists = objectMapper.readerFor(new TypeReference<List<SpotifyArtist>>() {}).readValue(payload);
      return artists.stream().map(a -> new SnapshotRankedItem(a.id(), a.name(), "ARTIST", a.rank())).toList();
    } catch (Exception ex) {
      throw new AppException("SNAPSHOT_PARSE_FAILED", "Failed to parse snapshot artists", HttpStatus.INTERNAL_SERVER_ERROR, List.of(ex.getMessage()));
    }
  }

  private List<SnapshotRankedItem> parseTracks(JsonNode payload) {
    try {
      List<SpotifyTrack> tracks = objectMapper.readerFor(new TypeReference<List<SpotifyTrack>>() {}).readValue(payload);
      return tracks.stream().map(t -> new SnapshotRankedItem(t.id(), t.name(), "TRACK", t.rank())).toList();
    } catch (Exception ex) {
      throw new AppException("SNAPSHOT_PARSE_FAILED", "Failed to parse snapshot tracks", HttpStatus.INTERNAL_SERVER_ERROR, List.of(ex.getMessage()));
    }
  }

  private TimeRange parseTimeRange(String value) {
    try {
      return TimeRange.parse(value);
    } catch (Exception ex) {
      throw new AppException("INVALID_TIME_RANGE", "timeRange must be short_term|medium_term|long_term", HttpStatus.BAD_REQUEST, List.of());
    }
  }
}
