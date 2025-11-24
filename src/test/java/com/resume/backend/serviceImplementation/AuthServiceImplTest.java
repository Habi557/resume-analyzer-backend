package com.resume.backend.serviceImplementation;

import com.resume.backend.configurations.JwtUtils;
import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.entity.Token;
import com.resume.backend.entity.TokenType;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.repository.TokenRepository;
import com.resume.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private UserEntity testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "password");
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        authentication = mock(Authentication.class);
       when(authentication.getName()).thenReturn("testuser");
//        when(authentication.getAuthorities()).thenReturn(
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//        );
    }

    @Test
    @Disabled
    void testLogin_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUserNameCaseSensitive(anyString()))
                .thenReturn(testUser);

        when(jwtUtils.generateToken(nullable(String.class))).thenReturn("testAccessToken");
        when(jwtUtils.generateRefreshToken(nullable(String.class))).thenReturn("testRefreshToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("testAccessToken", response.getAccessToken());
        assertEquals("testRefreshToken", response.getRefreshToken());
        assertFalse(response.getRoles().isEmpty());

        // Verify token was saved
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        Token savedToken = tokenCaptor.getValue();
        assertEquals("testAccessToken", savedToken.getToken());
        assertEquals(testUser, savedToken.getUser());
        assertEquals(TokenType.BEARER, savedToken.getTokenType());
        assertFalse(savedToken.isExpired());
        assertFalse(savedToken.isRevoked());
    }
}