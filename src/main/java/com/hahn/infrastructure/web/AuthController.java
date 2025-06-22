package com.hahn.infrastructure.web;

import com.hahn.application.dto.JwtTokenDto;
import com.hahn.application.dto.user.LoginRequest;
import com.hahn.application.dto.user.SignupRequest;
import com.hahn.infrastructure.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import com.hahn.application.security.AuthService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JwtTokenDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtTokenDto jwt = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtTokenDto> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("Refresh token is invalid!");
        }

        Authentication authentication = tokenProvider.getAuthentication(token);
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        return ResponseEntity.ok(new JwtTokenDto(newAccessToken, token));
    }
}
