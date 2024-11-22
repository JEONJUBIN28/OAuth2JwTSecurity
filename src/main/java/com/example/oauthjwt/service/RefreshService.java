package com.example.oauthjwt.service;

import com.example.oauthjwt.entity.RefreshEntity;
import com.example.oauthjwt.mapper.RefreshMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshMapper refreshMapper;

    public Optional<RefreshEntity> getRefreshEntityByAccessToken(String accessToken) {
        return refreshMapper.findByAccessToken(accessToken);
    }

    public Optional<RefreshEntity> getRefreshEntityByRefreshToken(String refreshToken) {
        return refreshMapper.findByRefreshToken(refreshToken);
    }

    public void deleteByRefreshToken(String refreshToken) {
        refreshMapper.deleteByRefreshToken(refreshToken);
    }
}
