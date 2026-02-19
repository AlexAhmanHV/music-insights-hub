package com.musicinsights.hub.integration;

import com.musicinsights.hub.auth.JwtService;
import com.musicinsights.hub.auth.SessionClaims;
import com.musicinsights.hub.spotify.SpotifyAccount;
import com.musicinsights.hub.spotify.SpotifyAccountRepository;
import com.musicinsights.hub.user.AppUser;
import com.musicinsights.hub.user.AppUserRepository;
import java.time.Instant;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InsightsIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private SpotifyAccountRepository spotifyAccountRepository;

  @Autowired
  private JwtService jwtService;

  private Cookie sessionCookie;

  @BeforeEach
  void setUp() {
    SpotifyAccount account = spotifyAccountRepository.findBySpotifyUserId("mock_user_001").orElseGet(() -> {
      AppUser user = appUserRepository.save(new AppUser());
      SpotifyAccount created = new SpotifyAccount();
      created.setAppUser(user);
      created.setSpotifyUserId("mock_user_001");
      created.setDisplayName("Mock Listener");
      created.setEmail("mock@example.com");
      created.setCountry("SE");
      return spotifyAccountRepository.save(created);
    });

    String token = jwtService.createToken(
        new SessionClaims(account.getAppUser().getId(), account.getId()),
        Instant.now().plusSeconds(3600));
    sessionCookie = new Cookie("session", token);
  }

  @Test
  void topInsightsShouldReturnFixtureData() throws Exception {
    mockMvc.perform(get("/insights/top").param("timeRange", "medium_term").cookie(sessionCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.topArtists[0].name").value("The Echoes"))
        .andExpect(jsonPath("$.topTracks[0].name").value("Midnight Signals"));
  }

  @Test
  void createSnapshotAndCompareTrendsShouldWork() throws Exception {
    String first = mockMvc.perform(post("/insights/snapshots")
            .cookie(sessionCookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"timeRange\":\"medium_term\"}"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String second = mockMvc.perform(post("/insights/snapshots")
            .cookie(sessionCookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"timeRange\":\"medium_term\"}"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String latestSnapshotId = second.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

    mockMvc.perform(get("/insights/trends").param("latestSnapshotId", latestSnapshotId).cookie(sessionCookie))
        .andExpect(status().isOk());
  }

  @Test
  void protectedEndpointsShouldRequireSession() throws Exception {
    mockMvc.perform(get("/insights/top").param("timeRange", "medium_term"))
        .andExpect(status().isUnauthorized());
  }
}
