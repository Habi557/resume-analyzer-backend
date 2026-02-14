package com.resume.backend.serviceImplementation;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.resume.backend.configurations.JwtUtils;
import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.Role;
import com.resume.backend.entity.Token;
import com.resume.backend.entity.TokenType;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.exceptions.TokenExpiredException;
import com.resume.backend.exceptions.UserAlreadyExistsException;
import com.resume.backend.repository.RoleRepository;
import com.resume.backend.repository.TokenRepository;
import com.resume.backend.repository.UserRepository;
import com.resume.backend.services.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CookieService cookieService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
        return buildAuthResponse(listOfRoles,accessToken,refreshToken,authenticate.getName());
    }



    public AuthResponse refreshToken(String refreshToken) {
        try {
            String username = jwtUtils.extractUsername(refreshToken);
            String password;
            UserEntity userEntity = userRepository.findByUserNameCaseSensitive(username);
            List<String> listOfRoles = userEntity.getRoles().stream().map(role -> role.getRoleName()).map(roleName -> new String(roleName)).toList();
            List<SimpleGrantedAuthority> listOfSimpleGrantedAuthority = listOfRoles.stream().map(roleName -> new SimpleGrantedAuthority(roleName)).toList();
            if(userEntity.getPassword()==null || userEntity.getPassword().isEmpty()){
                password = "{noop}OAUTH2_USER";
            }else{
                password = userEntity.getPassword();
            }
            User user = new User(username, password, listOfSimpleGrantedAuthority);
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

    @Override
    @Transactional
    public UserEntity register(UserDto userDto) {
       Optional<UserEntity> userEntityOptional = userRepository.findByEmail(userDto.getEmail());
        if(userEntityOptional.isPresent()){
            if(userDto.getProvider().equals("LOCAL")){
                throw new UserAlreadyExistsException("User already exists with this Email Id");
            }
            UserDto mapeduserDto = modelMapper.map(userEntityOptional.get(), UserDto.class);
            return  userEntityOptional.get();
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDto.getEmail());
        userEntity.setProvider(userDto.getProvider());
        userEntity.setProviderId(userDto.getProviderId());
        if("LOCAL".equals(userDto.getProvider())){
            if(userRepository.existsByUsername(userDto.getUsername())){
                throw new UserAlreadyExistsException("Username already exists try some other username");
            }
            userEntity.setUsername(userDto.getUsername());
            userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }else{
            String base = userDto.getEmail().split("@")[0];
            String uname;
            do {
                uname = base + "_" + RandomStringUtils.randomNumeric(4);
            } while (userRepository.existsByUsername(uname));

            userEntity.setUsername(uname);
            userEntity.setPassword(null);
        }
        Role role = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
        userEntity.setRoles(Set.of(role));
        applicationEventPublisher.publishEvent(userEntity);
        return userRepository.save(userEntity);
    }
    public AuthResponse buildAuthResponseFromRefreshToken(HttpServletRequest request) {

        String refresh = cookieService.getRefreshTokenFromCookie(request);

       // RefreshToken token = refreshTokenService.verify(refresh);
        String username = jwtUtils.extractUsername(refresh);
        UserEntity user = userRepository.findByUserNameCaseSensitive(username);
//        if(!username.equals(user.getUsername())){
//            throw new RuntimeException("Refresh token is not valid");
//        }
        String accessToken = jwtUtils.generateToken(username);

//        UserEntity user = token.getUser();
//
//        String accessToken = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                //.expiresIn(jwtService.getExpiry())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getRoleName).toList())
                .build();
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
