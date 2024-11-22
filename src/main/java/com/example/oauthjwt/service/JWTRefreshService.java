package com.example.oauthjwt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oauthjwt.entity.RefreshEntity;
import com.example.oauthjwt.mapper.RefreshTokenMapper;
import com.example.oauthjwt.filter.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JWTRefreshService {

    @Value("${spring.jwt.refresh.cookie.name}")
    private String REFRESH_COOKIE_NAME;

    @Value("${spring.jwt.access.expired}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${spring.jwt.refresh.expired}")
    private long REFRESH_TOKEN_EXPIRATION;

    private final RefreshTokenMapper refreshTokenMapper;  // JPA -> MyBatis로 변경
    private final JWTUtil jwtUtil;

    @Transactional
    public String[] refreshToken(Cookie[] cookies) {
        String refreshToken = extractRefreshToken(cookies);

        if (refreshToken == null) {
            return null;
        }

        RefreshEntity refreshEntity = validateRefreshToken(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, userId, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, userId, REFRESH_TOKEN_EXPIRATION);

        updateRefreshToken(refreshEntity, newAccessToken, newRefreshToken);

        return new String[]{newAccessToken, newRefreshToken};
    }

    private String extractRefreshToken(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private RefreshEntity validateRefreshToken(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtException("Refresh token expired");
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new JwtException("Invalid refresh token");
        }

        // MyBatis 매퍼를 통해 리프레시 토큰 조회
        return refreshTokenMapper.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Refresh token not found in database"));
    }

    private void updateRefreshToken(RefreshEntity oldEntity, String newAccessToken, String newRefreshToken) {
        // 기존 리프레시 토큰 삭제
        refreshTokenMapper.deleteByRefreshToken(oldEntity.getRefreshToken());

        // 새로운 리프레시 토큰 저장
        RefreshEntity newEntity = RefreshEntity.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(oldEntity.getUsername())
                .userId(oldEntity.getUserId())
                .role(oldEntity.getRole())
                .build();

        refreshTokenMapper.save(newEntity);
    }
}
