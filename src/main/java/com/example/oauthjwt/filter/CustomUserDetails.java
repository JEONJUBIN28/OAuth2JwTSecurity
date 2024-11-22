package com.example.oauthjwt.filter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.oauthjwt.entity.UserEntity;
import com.example.oauthjwt.mapper.UserMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });

        return authorities;
    }

    public Long getUserId() {
        return userEntity.getId();
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 기본값으로 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 기본값으로 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 기본값으로 true
    }

    @Override
    public boolean isEnabled() {
        return true; // 기본값으로 true
    }

    // MyBatis 매퍼를 통해 사용자 정보를 조회하는 메서드
    public static CustomUserDetails fromUsername(String username, UserMapper userMapper) {
        UserEntity userEntity = userMapper.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(userEntity);
    }
}
