package com.resume.backend.configurations;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.services.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private AuthService authService;
    private JwtUtils jwtUtils;
    @Value("${oauth2url}")
    private String oauth2url;
    public  OAuth2SuccessHandler(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth2User principal = authentication.getPrincipal();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");
        UserDto userDto = UserDto.builder().email(email).username(name).provider("GOOGLE").build();
        UserEntity register = authService.register(userDto);
       // String token = jwtUtils.generateToken(register.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(register.getUsername());
//        AuthResponse authResponse = AuthResponse.builder().accessToken(token).refreshToken(refreshToken)
//                .roles(register.getRoles().stream().map(role -> role.getRoleName()).map(roleName -> new String(roleName)).toList()).build();
        createRefreshTokenCookie(refreshToken,response);
        response.sendRedirect(oauth2url+"oauth-success");
       // response.getWriter().write("Login successful");


    }

    private void createRefreshTokenCookie(String refreshtoken, HttpServletResponse response) {
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshtoken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie",refreshCookie.toString());
    }
}
