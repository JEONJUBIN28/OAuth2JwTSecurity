package com.example.oauthjwt.mapper;

import com.example.oauthjwt.entity.RefreshEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.Optional;

@Mapper
public interface RefreshMapper {

    // accessToken을 이용해 RefreshEntity 조회
    @Select("SELECT * FROM refresh_tokens WHERE access_token = #{accessToken}")
    Optional<RefreshEntity> findByAccessToken(String accessToken);

    // refreshToken을 이용해 RefreshEntity 조회
    @Select("SELECT * FROM refresh_tokens WHERE refresh_token = #{refreshToken}")
    Optional<RefreshEntity> findByRefreshToken(String refreshToken);

    // refreshToken 삭제
    @Delete("DELETE FROM refresh_tokens WHERE refresh_token = #{refreshToken}")
    int deleteByRefreshToken(String refreshToken);
    
    @Insert("INSERT INTO refresh_tokens (access_token, refresh_token) VALUES (#{accessToken}, #{refreshToken})")
    void save(RefreshEntity refreshEntity);
}
