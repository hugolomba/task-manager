package com.hugo.taskmanager.security;

import com.hugo.taskmanager.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    public static final String BEARER_ = "Bearer ";

    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    public AuthTokenFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    try {
        String jwt = parseJwt(request); // take and parse the token
        // validate the token
        if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
            final String username = jwtUtil.getUserFromJwtToken(jwt); // extract username
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username); // get the user from database
            // create authentication
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            // add details in the previously created token
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // put the authentication token in the spring security context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    } catch (Exception e) {
        log.error("Cannot set user authentication: {}", e);
    }

    // keep the flow going to the next filter
    filterChain.doFilter(request, response); // needed for the next filter in the chain


    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith(BEARER_)) {
            return headerAuth.substring(BEARER_.length());
        }
        return null;
    }
}
