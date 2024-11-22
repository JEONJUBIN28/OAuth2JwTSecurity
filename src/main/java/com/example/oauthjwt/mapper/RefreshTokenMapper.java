package com.example.oauthjwt.mapper;

import com.example.oauthjwt.entity.RefreshEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    // 리프레시 토큰으로 사용자 정보 조회
    @Select("SELECT * FROM refresh_tokens WHERE refresh_token = #{refreshToken}")
    Optional<RefreshEntity> findByRefreshToken(String refreshToken);

    // 리프레시 토큰 삭제
    @Delete("DELETE FROM refresh_tokens WHERE refresh_token = #{refreshToken}")
    int deleteByRefreshToken(String refreshToken);

    // 리프레시 토큰과 관련된 데이터 삽입
    @Insert("INSERT INTO refresh_tokens (access_token, refresh_token, username, user_id, role) " +
            "VALUES (#{accessToken}, #{refreshToken}, #{username}, #{userId}, #{role})")
    int save(RefreshEntity refreshEntity);
}
