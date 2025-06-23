package com.hahn.application.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
