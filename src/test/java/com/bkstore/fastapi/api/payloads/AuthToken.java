package com.bkstore.fastapi.api.payloads;

import com.fasterxml.jackson.annotation.JsonProperty; // For mapping JSON fields if they differ from Java field names

public class AuthToken {
    @JsonProperty("access_token") // Matches "access_token" in JSON response
    private String accessToken;
    @JsonProperty("token_type") // Matches "token_type" in JSON response
    private String tokenType;

    public AuthToken() {
    }

    public AuthToken(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}