package com.app.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // CURRENT REQUEST PATH
        String path = request.getServletPath();

        // ALLOW AUTH ENDPOINTS WITHOUT TOKEN
        if (
                path.startsWith("/api/auth")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // GET AUTH HEADER
        final String authHeader = request.getHeader("Authorization");

        // NO TOKEN PRESENT
        if (
                authHeader == null ||
                        !authHeader.startsWith("Bearer ")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // EXTRACT JWT TOKEN
            final String jwt = authHeader.substring(7);

            // EXTRACT USERNAME / EMAIL
            final String userEmail = jwtService.extractUsername(jwt);

            // AUTHENTICATE USER
            if (
                    userEmail != null &&
                            SecurityContextHolder.getContext().getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(userEmail);

                // VALIDATE TOKEN
                if (
                        jwtService.isTokenValid(
                                jwt,
                                userDetails.getUsername()
                        )
                ) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // SET AUTHENTICATION
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception e) {

            // Ignore invalid token and continue request
            filterChain.doFilter(request, response);

            return;
        }

        // CONTINUE FILTER CHAIN
        filterChain.doFilter(request, response);
    }
}