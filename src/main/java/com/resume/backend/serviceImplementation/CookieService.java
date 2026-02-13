package com.resume.backend.serviceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;


@Service
public class CookieService {

    public String getRefreshTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

