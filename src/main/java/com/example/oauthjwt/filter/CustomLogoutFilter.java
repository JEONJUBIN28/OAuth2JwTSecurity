package com.example.oauthjwt.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import com.example.oauthjwt.service.LogoutService;

import java.io.IOException;
import java.util.Optional;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final LogoutService logoutService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Logout 접수");

        try {
            String refresh = getRefreshTokenFromCookie(request.getCookies());
            logoutService.validateRefreshToken(refresh);
            logoutService.logout(refresh);

            Cookie logoutCookie = logoutService.createLogoutCookie();
            response.addCookie(logoutCookie);
            response.setStatus(HttpServletResponse.SC_OK);

            log.info("로그아웃 완료.");
        } catch (JwtException e) {
            log.error("Logout failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().equals("/logout") && request.getMethod().equals("POST");
    }

    private String getRefreshTokenFromCookie(Cookie[] cookies) {
        return Optional.ofNullable(cookies)
                .flatMap(cookieArray -> Arrays.stream(cookieArray)
                        .filter(cookie -> "refresh".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
    }
}