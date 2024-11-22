package com.example.oauthjwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.example.oauthjwt.filter.CustomLogoutFilter;
import com.example.oauthjwt.filter.JWTFilter;
import com.example.oauthjwt.filter.JWTUtil;
import com.example.oauthjwt.filter.LoginFilter;
import com.example.oauthjwt.mapper.RefreshTokenMapper;
import com.example.oauthjwt.service.CustomOAuth2UserService;
import com.example.oauthjwt.service.LogoutService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private JWTUtil jwtUtil;  // JWTUtil 객체 주입

    @Autowired
    private AuthenticationManager authenticationManager;  // AuthenticationManager 주입

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;  // RefreshRepository 객체 주입

    @Autowired
    private LogoutService logoutService;  // LogoutService 객체 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화
        http.csrf().disable();

        // 기본 폼 로그인 비활성화
        http.formLogin().disable();

        // HTTP Basic 인증 비활성화
        http.httpBasic().disable();

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfoEndPoint ->
            userInfoEndPoint.userService(customOAuth2UserService))
        );

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(new LoginFilter(authenticationManager, jwtUtil, refreshTokenMapper), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new CustomLogoutFilter(logoutService), LogoutFilter.class);

        // 경로별 인가 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()  // 기본 경로는 누구나 접근 가능
                .anyRequest().authenticated());  // 나머지 경로는 인증된 사용자만 접근 가능

        // 세션 관리 설정 : STATELESS
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

}