package com.resume.backend.serviceImplementation;

import com.resume.backend.configurations.JwtUtils;
import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.entity.Token;
import com.resume.backend.entity.TokenType;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.exceptions.TokenExpiredException;
import com.resume.backend.repository.TokenRepository;
import com.resume.backend.repository.UserRepository;
import com.resume.backend.services.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl  implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
       // generating tokens
        String accessToken = jwtUtils.generateToken(authenticate.getName());
        String refreshToken = jwtUtils.generateRefreshToken(authenticate.getName());
        UserEntity user = userRepository.findByUserNameCaseSensitive(authenticate.getName());
        revokeAllUserTokens(user.getId());
        saveUserToken(user, accessToken);
        List<String> listOfRoles = user.getRoles().stream().map(role -> role.getRoleName()).map(roleName -> new String(roleName)).toList();

//        AuthResponse authResponse = AuthResponse.builder().roles(listOfRoles)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .tokenType("Bearer")
//                .expiresIn(1)
//                .username(authenticate.getName())
//                .build();
        return buildAuthResponse(listOfRoles,accessToken,refreshToken,authenticate.getName());
        //return authResponse;
    }



    public AuthResponse refreshToken(String refreshToken) {
        try {
            String username = jwtUtils.extractUsername(refreshToken);
            UserEntity userEntity = userRepository.findByUserNameCaseSensitive(username);
            List<String> listOfRoles = userEntity.getRoles().stream().map(role -> role.getRoleName()).map(roleName -> new String(roleName)).toList();
            List<SimpleGrantedAuthority> listOfSimpleGrantedAuthority = listOfRoles.stream().map(roleName -> new SimpleGrantedAuthority(roleName)).toList();
            User user = new User(username, userEntity.getPassword(), listOfSimpleGrantedAuthority);
            if (!jwtUtils.validateToken(refreshToken, user)) {
                throw new TokenExpiredException("Refresh token is not valid");
            }
            String accessToken = jwtUtils.generateToken(username);
            revokeAllUserTokens(userEntity.getId());
            saveUserToken(userEntity, accessToken);
            return buildAuthResponse(listOfRoles,accessToken,refreshToken, username);
        }catch (ExpiredJwtException ex){
            throw new TokenExpiredException("Refresh token is expired Login agian");
        }

    }

    @Override
    public void logout(String accessToken) {
        Optional<Token> byToken = tokenRepository.findByToken(accessToken);
        if(byToken.isPresent()) {
            Token token = byToken.get();
            //token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
        }
    }

    public void saveUserToken(UserEntity user, String accessToken) {
        Token token = Token.builder()
                .user(user)
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(Long userId) {
        List<Token> tokens = tokenRepository.findAllByUserIdAndExpiredFalseAndRevokedFalse(userId);
        if (tokens.isEmpty()) {
            return;
        }
        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(tokens);
    }
    private AuthResponse buildAuthResponse(List<String> listOfRoles, String accessToken, String refreshToken, String name) {
        return AuthResponse.builder().roles(listOfRoles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(1)
                .username(name)
                .build();
    }
}
