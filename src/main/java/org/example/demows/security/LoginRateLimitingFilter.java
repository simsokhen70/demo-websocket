package org.example.demows.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demows.dto.ApiResponse;
import org.example.demows.dto.ErrorResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory rate limiter for login endpoint to mitigate brute force.
 * Not cluster-safe; replace with Redis/Bucket4j in production for distributed setups.
 */
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final int maxRequests;
    private final long windowSeconds;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static class Window {
        volatile long windowStartEpochSec;
        volatile int count;
    }

    private final Map<String, Window> ipToWindow = new ConcurrentHashMap<>();

    public LoginRateLimitingFilter(int maxRequests, long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientKey = resolveClientKey(request);
        long now = Instant.now().getEpochSecond();

        Window window = ipToWindow.computeIfAbsent(clientKey, k -> {
            Window w = new Window();
            w.windowStartEpochSec = now;
            w.count = 0;
            return w;
        });

        synchronized (window) {
            if (now - window.windowStartEpochSec >= windowSeconds) {
                window.windowStartEpochSec = now;
                window.count = 0;
            }
            window.count++;
            if (window.count > maxRequests) {
                respondTooManyRequests(response, request.getRequestURI());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void respondTooManyRequests(HttpServletResponse response, String path) throws IOException {
        String traceId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ErrorResponse err = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(429)
                .error("Too Many Requests")
                .message("Login rate limit exceeded. Please try again later.")
                .path(path)
                .suggestion("Wait before retrying, or contact support if this persists.")
                .build();
        ApiResponse<ErrorResponse> body = ApiResponse.error("Too many requests", err);
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}


