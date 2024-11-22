package com.example.oauthjwt.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{
	
	private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("properties");
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return attribute.get("nickname").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("nickname").toString();
    }

    @Override
    public String getName() {

        return attribute.get("profile_image").toString();
    }

}
