package com.hahn.application.dto;

import lombok.Data;

@Data
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
