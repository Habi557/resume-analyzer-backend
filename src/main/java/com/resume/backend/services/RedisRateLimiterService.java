package com.resume.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRateLimiterService {
    private final StringRedisTemplate redisTemplate;
    public  boolean isAllowed(String username, String apiType, int limit, Duration duration){
        String key = "rate_limit:" + username + ":" + apiType;
        Long count =redisTemplate.opsForValue().increment(key);
        if(count == null){
            return false;
        }
        if(count == 1){
            redisTemplate.expire(key, duration);
        }
        return count <= limit;

    }
}
