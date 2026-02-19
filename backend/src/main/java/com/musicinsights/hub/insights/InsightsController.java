package com.musicinsights.hub.insights;

import com.musicinsights.hub.auth.SessionClaims;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insights")
public class InsightsController {

  private final InsightsService insightsService;

  public InsightsController(InsightsService insightsService) {
    this.insightsService = insightsService;
  }

  @GetMapping("/top")
  public TopInsightsResponse top(@RequestParam String timeRange, Authentication authentication) {
    SessionClaims claims = (SessionClaims) authentication.getPrincipal();
    return insightsService.getTopInsights(claims.spotifyAccountId(), timeRange);
  }

  @PostMapping("/snapshots")
  public SnapshotSummary createSnapshot(@Valid @RequestBody SnapshotCreateRequest request, Authentication authentication) {
    SessionClaims claims = (SessionClaims) authentication.getPrincipal();
    return insightsService.createSnapshot(claims.spotifyAccountId(), request.timeRange());
  }

  @GetMapping("/snapshots")
  public List<SnapshotSummary> listSnapshots(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, Authentication authentication) {
    SessionClaims claims = (SessionClaims) authentication.getPrincipal();
    return insightsService.listSnapshots(claims.spotifyAccountId(), page, size);
  }

  @GetMapping("/trends")
  public TrendsResponse trends(@RequestParam UUID latestSnapshotId, Authentication authentication) {
    SessionClaims claims = (SessionClaims) authentication.getPrincipal();
    return insightsService.compareSnapshots(claims.spotifyAccountId(), latestSnapshotId);
  }
}
