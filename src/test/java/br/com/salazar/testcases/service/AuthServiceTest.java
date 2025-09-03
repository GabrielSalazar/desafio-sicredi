package br.com.salazar.testcases.service;

import br.com.salazar.model.dto.LoginRequestDto;
import br.com.salazar.model.dto.LoginResponseDto;
import br.com.salazar.service.AuthService;
import br.com.salazar.exception.AuthenticationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "baseUrl", "https://dummyjson.com");
    }

    @Test
    @DisplayName("authenticate with valid credentials returns LoginResponseDto")
    void authenticate_ValidCredentials_ReturnsLoginResponse() {
        // Given
        LoginRequestDto request = new LoginRequestDto("emilys", "emilyspass");
        LoginResponseDto expectedResponse = new LoginResponseDto(
                1L, "emilys", "emily.johnson@x.dummyjson.com",
                "Emily", "Johnson", "female",
                "https://dummyjson.com/icon/emilys/128",
                "token123", "refresh123"
        );

        ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/auth/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(LoginResponseDto.class)
        )).thenReturn(response);

        // When
        LoginResponseDto result = authService.authenticate(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("emilys");
        assertThat(result.getToken()).isEqualTo("token123");
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(LoginResponseDto.class));
    }

    @Test
    @DisplayName("authenticate with invalid credentials throws AuthenticationException")
    void authenticate_InvalidCredentials_ThrowsAuthenticationException() {
        // Given
        LoginRequestDto request = new LoginRequestDto("invalid", "invalid");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized",
                HttpHeaders.EMPTY,
                "{\"message\":\"Invalid credentials\"}".getBytes(),
                null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(LoginResponseDto.class)))
                .thenThrow(ex);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
