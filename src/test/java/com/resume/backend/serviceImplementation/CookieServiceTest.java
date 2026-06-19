package com.resume.backend.serviceImplementation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CookieServiceTest {

    private final CookieService cookieService = new CookieService();

    @Test
    void getRefreshTokenFromCookie_returnsRefreshTokenValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", "access-token"), new Cookie("refreshToken", "refresh-token"));

        String result = cookieService.getRefreshTokenFromCookie(request);

        assertEquals("refresh-token", result);
    }

    @Test
    void getRefreshTokenFromCookie_returnsNullWhenCookiesAreMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String result = cookieService.getRefreshTokenFromCookie(request);

        assertNull(result);
    }

    @Test
    void getRefreshTokenFromCookie_returnsNullWhenRefreshTokenIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", "access-token"));

        String result = cookieService.getRefreshTokenFromCookie(request);

        assertNull(result);
    }
}
