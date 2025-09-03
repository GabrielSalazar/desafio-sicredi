package br.com.salazar.testcases.controller;

import br.com.salazar.controller.AuthController;
import br.com.salazar.model.dto.LoginRequestDto;
import br.com.salazar.model.dto.LoginResponseDto;
import br.com.salazar.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private LoginResponseDto dummyResponse;

    @BeforeEach
    void setUp() {
        dummyResponse = new LoginResponseDto(
                1L, "emilys", "emily.johnson@x.dummyjson.com",
                "Emily", "Johnson", "female",
                "https://dummyjson.com/icon/emilys/128",
                "token123", "refresh123"
        );
    }

    @Test
    @DisplayName("login with valid request returns 201 Created")
    void login_ValidRequest_ReturnsCreatedWithBody() throws Exception {
        // Arrange
        LoginRequestDto reqDto = new LoginRequestDto("emilys", "emilyspass");
        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenReturn(dummyResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("emilys"))
                .andExpect(jsonPath("$.token").value("token123"));

        verify(authService, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("login with invalid request returns 401 Unauthorized")
    void login_InvalidRequest_ReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequestDto reqDto = new LoginRequestDto("teste", "teste123");
        when(authService.authenticate(any()))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).authenticate(any());
    }
}
