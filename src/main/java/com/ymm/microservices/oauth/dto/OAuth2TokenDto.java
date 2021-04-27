package com.ymm.microservices.oauth.dto;



import java.io.Serializable;


public class OAuth2TokenDto implements Serializable {
    private static final Long serialVersionId = 1L;

    private String accessToken;
    private String refreshToken;
    private Integer expiration;
    private String tokenType = "bearer";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getExpiration() {
        return expiration;
    }

    public void setExpiration(Integer expiration) {
        this.expiration = expiration;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
