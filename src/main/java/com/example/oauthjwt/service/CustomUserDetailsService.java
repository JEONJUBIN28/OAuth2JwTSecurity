package com.example.oauthjwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.oauthjwt.entity.UserEntity;
import com.example.oauthjwt.filter.CustomUserDetails;
import com.example.oauthjwt.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // MyBatis를 통해 사용자 정보를 조회
        UserEntity userEntity = userMapper.findByUsername(username);
        
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // CustomUserDetails 객체로 변환하여 반환
        return new CustomUserDetails(userEntity);
    }
}
