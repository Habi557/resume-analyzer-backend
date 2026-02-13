package com.resume.backend.configurations;

import com.resume.backend.exceptions.JwtExpiredAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.bridge.IMessage;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String message;
        if (request.getAttribute("JWT_EXPIRED") != null) {
            message = "Token expired. Please login again.";
        } else {
            message = "Please login to access this resource.";
        }

        response.getWriter().write("""
        {
          "status": 401,
          "error": "Unauthorized",
          "message": "%s"
        }
        """.formatted(message));
    }
}

