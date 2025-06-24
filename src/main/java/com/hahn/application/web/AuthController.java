package com.hahn.application.web;

import com.hahn.application.dto.common.ApiResponse;
import com.hahn.application.dto.common.JwtTokenDto;
import com.hahn.application.dto.authentication.LoginDto;
import com.hahn.application.dto.authentication.SignupDto;
import com.hahn.domain.exception.UnauthorizedException;
import com.hahn.infrastructure.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.hahn.application.service.AuthService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtTokenDto>> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        JwtTokenDto jwt = authService.authenticateUser(loginDto);
        return ResponseEntity.ok(ApiResponse.success("User authenticated successfully", jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody SignupDto signUpDto) {
        authService.registerUser(signUpDto);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<ApiResponse<JwtTokenDto>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Refresh token is invalid!");
        }

        Authentication authentication = tokenProvider.getAuthentication(token);
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Token refreshed successfully",
                        new JwtTokenDto(newAccessToken, token, null)
                )
        );
    }
}