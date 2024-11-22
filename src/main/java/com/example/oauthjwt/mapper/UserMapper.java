package com.example.oauthjwt.mapper;

import com.example.oauthjwt.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 사용자 이름으로 사용자 정보 조회
    @Select("SELECT * FROM users WHERE username = #{username}")
    UserEntity findByUsername(String username);
}
