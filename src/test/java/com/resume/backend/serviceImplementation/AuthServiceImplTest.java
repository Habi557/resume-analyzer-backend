package com.resume.backend.serviceImplementation;

import com.resume.backend.configurations.JwtUtils;
import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.entity.Role;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    @Spy
    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private UserEntity testUser;
    private Authentication authentication;
    private  Token token;
    private List<Token> listOfTokens;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("habi", "habi123");
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("habi");
        testUser.setPassword("habi123");
        testUser.setRoles(Set.of(new Role(1l,"ROLE_USER"),new Role(2l,"ROLE_ADMIN")));
        token = new Token();
        token.setId(1L);
        token.setToken("token");
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
       listOfTokens= List.of(token);

        authentication = mock(Authentication.class);
      // List<SimpleGrantedAuthority> listOfRoles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
      // authentication.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).toList();
       //when(authentication.getAuthorities()).thenReturn(anyList());
//        when(authentication.getAuthorities()).thenReturn(
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//        );
    }


    @Test
    void testLogin_Success() {
        when(authentication.getName()).thenReturn("habi");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken(anyString())).thenReturn("access_token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh_token");
        when(userRepository.findByUserNameCaseSensitive(anyString())).thenReturn(testUser);

        // when(authService.revokeAllUserTokens(anyLong())).

        AuthResponse login = authService.login(loginRequest);
        assertEquals("access_token", login.getAccessToken());
        assertEquals("refresh_token", login.getRefreshToken());
        assertEquals("Bearer", login.getTokenType());
        assertEquals(1, login.getExpiresIn());
        assertEquals("habi", login.getUsername());
        assertEquals(2,login.getRoles().size());
        assertTrue(login.getRoles().contains("ROLE_USER"));
        assertTrue(login.getRoles().contains("ROLE_ADMIN"));
        // verify that revokeAllUserTokens is called
        verify(authService).revokeAllUserTokens(testUser.getId());
        // verify that saveUserToken is called
        verify(authService).saveUserToken(testUser, "access_token");

    }
    @Test
    void testRefreshToken_Success() {
        when(jwtUtils.extractUsername(anyString())).thenReturn("habi");
        when(userRepository.findByUserNameCaseSensitive(anyString())).thenReturn(testUser);
        when(jwtUtils.validateToken(anyString(), any(User.class))).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn("access_token");

        AuthResponse authResponse = authService.refreshToken("refresh_token");
        assertEquals("access_token", authResponse.getAccessToken());
        assertEquals("refresh_token", authResponse.getRefreshToken());
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(1, authResponse.getExpiresIn());
        assertEquals("habi", authResponse.getUsername());
        assertTrue(authResponse.getRoles().contains("ROLE_USER"));
        assertTrue(authResponse.getRoles().contains("ROLE_ADMIN"));
        verify(authService).revokeAllUserTokens(anyLong());
        verify(authService).saveUserToken(testUser, "access_token");


    }
}