package com.resume.backend.dtos;

import java.time.Duration;

public record RateLimitRule(String name, int limit, Duration duration) {
}
