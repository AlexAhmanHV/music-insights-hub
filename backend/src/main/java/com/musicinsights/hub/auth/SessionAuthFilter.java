package com.musicinsights.hub.auth;

import com.musicinsights.hub.common.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public SessionAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    SecurityContextHolder.clearContext();
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(c -> "session".equals(c.getName()))
          .findFirst()
          .ifPresent(cookie -> {
            try {
              SessionClaims claims = jwtService.parseToken(cookie.getValue());
              UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims, null, java.util.List.of());
              SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (AppException ex) {
              if (ex.getStatus() != HttpStatus.UNAUTHORIZED) {
                throw ex;
              }
              SecurityContextHolder.clearContext();
            }
          });
    }
    filterChain.doFilter(request, response);
  }
}
