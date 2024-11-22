package com.example.oauthjwt.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshEntity {
	private Long id;
    private String accessToken;
    private String refreshToken;
    private String username;
    private Long userId;
    private String role;
}
