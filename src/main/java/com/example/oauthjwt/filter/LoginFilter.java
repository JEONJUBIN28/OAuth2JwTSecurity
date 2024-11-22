package com.example.oauthjwt.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.oauthjwt.entity.RefreshEntity;
import com.example.oauthjwt.mapper.RefreshTokenMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private final RefreshTokenMapper refreshTokenMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
//        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            Map<String, String> requestMap = objectMapper.readValue(req.getInputStream(), Map.class);
            String username = requestMap.get("username");
            String password = requestMap.get("password");
            log.info("Attempting authentication for user: {}", username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
            return this.authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth){
        String username = auth.getName();

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();
        String role = grantedAuthority.getAuthority();

        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        Long userId = details.getUserId();

        String access = jwtUtil.createJwt("access", username, role, userId, 1800000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, userId, 86400000L);

        setJsonTokens(refresh, access);

        res.setHeader("authorization", access);
        res.addCookie(createCookie("refresh", refresh));

        res.setStatus(HttpStatus.OK.value());

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);

    }

    private void setJsonTokens(String refresh, String access) {
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .refreshToken(refresh)
                .accessToken(access)
                .build();
        refreshTokenMapper.save(refreshEntity);
    }
}
