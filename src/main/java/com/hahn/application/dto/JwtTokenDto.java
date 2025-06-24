package com.hahn.application.dto;

import com.hahn.application.dto.user.UserDto;
import lombok.Data;

@Data
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
    private UserDto user;

    public JwtTokenDto(String accessToken, String refreshToken, UserDto userDto) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = userDto;
    }
}
