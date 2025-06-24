package com.hahn;

import com.hahn.application.dto.common.JwtTokenDto;
import com.hahn.application.dto.authentication.LoginDto;
import com.hahn.application.dto.authentication.SignupDto;
import com.hahn.application.dto.user.UserDto;
import com.hahn.application.service.AuthService;
import com.hahn.domain.enums.Role;
import com.hahn.domain.exception.ConflictException;
import com.hahn.domain.exception.ResourceNotFoundException;
import com.hahn.domain.exception.UnauthorizedException;
import com.hahn.domain.model.User;
import com.hahn.domain.repository.UserRepository;
import com.hahn.infrastructure.jwt.JwtTokenProvider;
import com.hahn.infrastructure.persistence.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private final String testUsername = "testuser";
    private final String testEmail = "test@example.com";
    private final String testPassword = "password";
    private final String encodedPassword = "encodedPassword";
    private final String accessToken = "accessToken";
    private final String refreshToken = "refreshToken";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateUser_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(testUsername);
        loginDto.setPassword(testPassword);
        Authentication authentication = mock(Authentication.class);
        User user = User.create(testUsername, testEmail, encodedPassword, Set.of(Role.ROLE_ADMIN));
        UserDto userDto = UserMapper.toDto(user);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication))
                .thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(authentication))
                .thenReturn(refreshToken);
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.of(user));

        JwtTokenDto result = authService.authenticateUser(loginDto);

        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        assertEquals(userDto.getUsername(), result.getUser().getUsername());
        assertEquals(userDto.getEmail(), result.getUser().getEmail());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));
        verify(tokenProvider).createAccessToken(authentication);
        verify(tokenProvider).createRefreshToken(authentication);
        verify(userRepository).findByUsername(testUsername);
    }

    @Test
    void authenticateUser_InvalidCredentials_ThrowsUnauthorizedException() {
        LoginDto loginDto = new LoginDto();
        loginDto.setPassword("wrongpassword");
        loginDto.setUsername(testUsername);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(UnauthorizedException.class, () -> {
            authService.authenticateUser(loginDto);
        });

        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void registerUser_Success() {
        SignupDto signupDto = new SignupDto();
        signupDto.setPassword(testPassword);
        signupDto.setEmail(testEmail);
        signupDto.setUsername(testUsername);
        signupDto.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.existsByUsername(testUsername)).thenReturn(false);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

        authService.registerUser(signupDto);

        verify(userRepository).existsByUsername(testUsername);
        verify(userRepository).existsByEmail(testEmail);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameTaken_ThrowsConflictException() {
        SignupDto signupDto = new SignupDto();

        signupDto.setUsername(testUsername);
        signupDto.setRoles(Set.of(Role.ROLE_USER));
        signupDto.setEmail(testEmail);
        signupDto.setPassword(testPassword);

        when(userRepository.existsByUsername(testUsername)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            authService.registerUser(signupDto);
        });

        verify(userRepository).existsByUsername(testUsername);
        verify(userRepository, never()).existsByEmail(any());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_EmailInUse_ThrowsConflictException() {
        SignupDto signupDto = new SignupDto();

        signupDto.setUsername(testUsername);
        signupDto.setRoles(Set.of(Role.ROLE_USER));
        signupDto.setEmail(testEmail);
        signupDto.setPassword(testPassword);

        when(userRepository.existsByUsername(testUsername)).thenReturn(false);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            authService.registerUser(signupDto);
        });

        verify(userRepository).existsByUsername(testUsername);
        verify(userRepository).existsByEmail(testEmail);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentUser_Success() {
        User user = User.create(testUsername, testEmail, encodedPassword, Set.of(Role.ROLE_USER));
        UserDto expectedDto = UserMapper.toDto(user);

        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.of(user));

        UserDto result = authService.getCurrentUser(testUsername);

        assertNotNull(result);
        assertEquals(expectedDto.getUsername(), result.getUsername());
        assertEquals(expectedDto.getEmail(), result.getEmail());

        verify(userRepository).findByUsername(testUsername);
    }

    @Test
    void getCurrentUser_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findByUsername(testUsername))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            authService.getCurrentUser(testUsername);
        });

        verify(userRepository).findByUsername(testUsername);
    }
}