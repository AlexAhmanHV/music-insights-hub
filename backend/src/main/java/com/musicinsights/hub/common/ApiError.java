package com.musicinsights.hub.common;

import java.util.List;

public record ApiError(String errorCode, String message, List<String> details) {}
