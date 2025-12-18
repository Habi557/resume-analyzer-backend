package com.resume.backend.configurations;

import java.io.IOException;
import java.util.List;

import com.resume.backend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//import com.olx.user.exception.TokenAlreadyExpired;

import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.impl.DefaultClaims;
//import io.jsonwebtoken.impl.DefaultJwsHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private JwtUtils jwtService;
	private UserDetailsService userDetailsService;
	private TokenRepository tokenRepository;

	public JwtAuthFilter(JwtUtils jwtService, UserDetailsService userDetailsService,TokenRepository tokenRepository) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.tokenRepository = tokenRepository;
	}
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth")
                || path.equals("/")
                || path.equals("/health")
                || path.startsWith("/error")
                || path.startsWith("/favicon")
                || path.contains("swagger")
                || path.contains("api-docs");
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String servletPath = request.getServletPath();
		System.out.println("PATH → " + servletPath);
//		if(servletPath.startsWith("/auth/login") || servletPath.startsWith("/auth/refreshToken") || servletPath.startsWith("/auth/logout")) {
//			filterChain.doFilter(request, response);
//			return;
//		}
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				username = jwtService.extractUsername(token);
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				Boolean isStoredValid = tokenRepository.findByToken(token).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
                tokenRepository.findByToken(token)
                        .ifPresentOrElse(
                                t -> System.out.println("DB TOKEN FOUND"),
                                () -> System.out.println("DB TOKEN NOT FOUND")
                        );
				if (jwtService.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
			filterChain.doFilter(request, response);

		} catch (ExpiredJwtException e) {
			System.out.println("token val");
            SecurityContextHolder.clearContext();
            throw new org.springframework.security.authentication.CredentialsExpiredException(
                    "JWT token expired", e);
			//handleExpiredToken(response, e);

		} 

	}

	// Custom method to handle expired tokens
	private void handleExpiredToken(HttpServletResponse response, ExpiredJwtException e) throws IOException {
		 response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Token Expired Login again");
	        
	}

}