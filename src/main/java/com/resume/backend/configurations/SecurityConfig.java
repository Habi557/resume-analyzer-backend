package com.resume.backend.configurations;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private JwtAuthFilter jwtAuthFilter;
    private AuthenticationSuccessHandler oAuth2SuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.oAuth2SuccessHandler=authenticationSuccessHandler;
        this.authenticationFailureHandler=authenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors ->cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
               // .csrf(csrf -> csrf
                        //.ignoringRequestMatchers("/auth/login","/auth/refreshToken","/auth/logout","/auth/register","/user/getUserAnalyisedDetails","/oauth2/authorization/google","/login/oauth2/code/google","/oauth-success","/ai/upload"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login","/auth/refreshToken","/auth/logout","/auth/register","/user/getUserAnalyisedDetails", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/", "/health", "/actuator/health").permitAll()
                        //.requestMatchers("/ai/upload").hasAnyRole("USER","ADMIN")
                         .requestMatchers("/ai/screen-resume").hasRole("ADMIN")
                        .requestMatchers("/chatbot/query").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2->
                        //oauth2.loginPage("/oauth2/authorization/google")
                        oauth2.successHandler(oAuth2SuccessHandler)
                                .failureHandler(authenticationFailureHandler)
                        )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.accessDeniedHandler(new CustomAccessDeniedHandler()).authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200","http://localhost","http://myprojectforangular.s3-website.ap-south-1.amazonaws.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Set-Cookie","Content-Disposition", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}

