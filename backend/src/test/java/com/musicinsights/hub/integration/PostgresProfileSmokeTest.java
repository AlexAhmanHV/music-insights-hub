package com.musicinsights.hub.integration;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "MUSICHUB_TEST_DB_URL", matches = "jdbc:postgresql:.*")
class PostgresProfileSmokeTest {
  // This test class ensures local Postgres integration can be enabled with MUSICHUB_TEST_DB_URL.
}
