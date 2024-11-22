package com.example.oauthjwt.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    // 기타 필요한 필드들
	
}
