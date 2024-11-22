package com.example.oauthjwt.model;

public class RefreshToken {

    private String token;
    private String userId;  // 예시로 추가된 필드 (데이터베이스 컬럼과 일치하도록)

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 기타 필요한 필드와 메서드
}
