package com.resume.backend.configurations;

import com.resume.backend.dtos.RateLimitRule;
import com.resume.backend.services.RedisRateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRateLimitFilter extends OncePerRequestFilter {
    private final RedisRateLimiterService rateLimiter;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)){
            String userName = authentication.getName();
            RateLimitRule rateLimitRule = resolveRule(request);
            boolean isAllowed = rateLimiter.isAllowed(userName, rateLimitRule.name(),rateLimitRule.limit(),rateLimitRule.duration());
            log.info("Rate limit check for user {} and rule {}", userName, rateLimitRule.name());
            if(!isAllowed){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("""
                    {
                      "status": 429,
                      "message": "Rate limit exceeded"
                    }
                    """);
                log.info("Rate limit exceeded for user {} and rule {}", userName, rateLimitRule.name());
                return;
            }

        }
        filterChain.doFilter(request, response);

    }

    private RateLimitRule resolveRule(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if(uri.contains("/ai")){
            return new RateLimitRule("AI ANALYSIS", 10, Duration.ofMinutes(1));
        }
        if(uri.contains("/chatbot")){
            return new RateLimitRule("CHATBOT", 10, Duration.ofMinutes(1));
        }
        return new RateLimitRule("DEFAULT", 20, Duration.ofMinutes(1));
        
    }
}
