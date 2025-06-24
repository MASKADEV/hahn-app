package com.hahn.application.service;

import com.hahn.application.dto.JwtTokenDto;
import com.hahn.application.dto.user.LoginDto;
import com.hahn.application.dto.user.SignupDto;
import com.hahn.application.dto.user.UserDto;
import com.hahn.domain.exception.ConflictException;
import com.hahn.domain.exception.ResourceNotFoundException;
import com.hahn.domain.exception.UnauthorizedException;
import com.hahn.domain.model.User;
import com.hahn.domain.repository.UserRepository;
import com.hahn.infrastructure.jwt.JwtTokenProvider;
import com.hahn.infrastructure.persistence.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public JwtTokenDto authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            UserDto userDto = getCurrentUser(loginDto.getUsername());

            return new JwtTokenDto(accessToken, refreshToken, userDto);
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public void registerUser(SignupDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new ConflictException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new ConflictException("Email is already in use!");
        }

        User user = User.create(
                signUpDto.getUsername(),
                signUpDto.getEmail(),
                passwordEncoder.encode(signUpDto.getPassword()),
                signUpDto.getRoles());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toDto(user);
    }
}