package com.hahn.application.security;

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
import com.hahn.infrastructure.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public JwtTokenDto authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            return new JwtTokenDto(accessToken, refreshToken);
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

    public UserDto getCurrentUser(UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toDto(user);
    }

    private Set<String> getRoles(Set<String> strRoles) {
        Set<String> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add("ROLE_USER");
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    roles.add("ROLE_ADMIN");
                } else {
                    roles.add("ROLE_USER");
                }
            });
        }

        return roles;
    }
}