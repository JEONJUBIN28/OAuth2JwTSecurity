package com.example.oauthjwt.service;

import com.example.oauthjwt.entity.RefreshEntity;
import com.example.oauthjwt.mapper.RefreshMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService {

    private final RefreshMapper refreshMapper;  // MyBatis Mapper 주입

    public LogoutService(RefreshMapper refreshMapper) {
        this.refreshMapper = refreshMapper;
    }

    @Transactional
    public void validateRefreshToken(String refreshToken) {
        RefreshEntity refreshEntity = refreshMapper.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        // 여기서 토큰 만료 여부나 추가적인 검증 로직을 작성할 수 있습니다.
        try {
            // JWT 검증 (예: 만료 시간 체크)
            // jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtException("Refresh token expired");
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        // 리프레시 토큰을 삭제
        refreshMapper.deleteByRefreshToken(refreshToken);
    }

    public Cookie createLogoutCookie() {
        // 로그아웃 쿠키 생성 로직
        Cookie logoutCookie = new Cookie("refresh", "");
        logoutCookie.setMaxAge(0); // 쿠키 만료 시간 0으로 설정
        logoutCookie.setPath("/");  // 쿠키의 유효 경로 설정
        return logoutCookie;
    }
}
