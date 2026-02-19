package com.musicinsights.hub.auth;

import java.util.UUID;

public record SessionClaims(UUID appUserId, UUID spotifyAccountId) {}
