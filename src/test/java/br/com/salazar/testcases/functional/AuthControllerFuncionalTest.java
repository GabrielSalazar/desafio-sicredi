package br.com.salazar.testcases.functional;

import br.com.salazar.controller.AuthController;
import br.com.salazar.model.dto.LoginRequestDto;
import br.com.salazar.model.dto.LoginResponseDto;
import br.com.salazar.service.AuthService;
import br.com.salazar.exception.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(AuthController.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("🔐 Auth Controller - Functional Tests")
class AuthControllerFunctionalTest {

    private static final int MAX_USERNAME_LENGTH = 50;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper classes for building test data
    public static class LoginRequestBuilder {
        private String username = "defaultuser";
        private String password = "defaultpass";

        public static LoginRequestBuilder aLoginRequest() {
            return new LoginRequestBuilder();
        }

        public LoginRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public LoginRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public LoginRequestBuilder withValidCredentials() {
            this.username = "emilys";
            this.password = "emilyspass";
            return this;
        }

        public LoginRequestDto build() {
            return new LoginRequestDto(username, password);
        }
    }

    public static class LoginResponseBuilder {
        public static LoginResponseDto validToken() {
            return new LoginResponseDto(
                    1L, "emilys", "emily.johnson@x.dummyjson.com",
                    "Emily", "Johnson", "female",
                    "https://dummyjson.com/icon/emilys/128",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.valid.token",
                    "refresh123"
            );
        }

        public static LoginResponseDto withToken(String token) {
            return new LoginResponseDto(
                    1L, "testuser", "test@example.com",
                    "Test", "User", "male",
                    "https://example.com/avatar.jpg",
                    token, "refresh123"
            );
        }
    }

    // ✅ TESTES POSITIVOS
    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    void shouldAuthenticateWithValidCredentials() throws Exception {
        // Given
        LoginRequestDto request = LoginRequestBuilder.aLoginRequest()
                .withValidCredentials()
                .build();
        LoginResponseDto response = LoginResponseBuilder.validToken();

        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andDo(print());

        verify(authService, times(1)).authenticate(any(LoginRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user", "user123", "user_name", "user-name", "user.name",
            "User", "USER", "user@domain.com"
    })
    @DisplayName("Should handle various valid username formats")
    void shouldHandleValidUsernameFormats(String username) throws Exception {
        // Given
        LoginRequestDto request = LoginRequestBuilder.aLoginRequest()
                .withUsername(username)
                .withPassword("validpass123")
                .build();
        LoginResponseDto response = LoginResponseBuilder.validToken();

        when(authService.authenticate(any(LoginRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());

        verify(authService).authenticate(argThat(req -> req.getUsername().equals(username)));
    }

    // ❌ TESTES NEGATIVOS
    @Test
    @DisplayName("Should reject invalid credentials with proper error response")
    void shouldRejectInvalidCredentials() throws Exception {
        // Given
        LoginRequestDto request = LoginRequestBuilder.aLoginRequest()
                .withUsername("invaliduser")
                .withPassword("wrongpass")
                .build();

        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        verify(authService, times(1)).authenticate(any(LoginRequestDto.class));
    }

    @ParameterizedTest
    @CsvSource({
            "'', validpass123",
            "validuser, ''",
            ", validpass123",
            "validuser, "
    })
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields(String username, String password) throws Exception {
        // Given
        LoginRequestDto request = new LoginRequestDto(username, password);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").isString());

        verify(authService, never()).authenticate(any(LoginRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{invalid json}",
            "{\"username\":\"test\", \"password\":}",
            "not json at all",
            ""
    })
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJSON(String malformedJson) throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticate(any(LoginRequestDto.class));
    }

    // 🎯 TESTES DE EDGE CASES
    @ParameterizedTest
    @ValueSource(ints = {254, 255, 256})
    @DisplayName("Should handle username length limits correctly")
    void shouldHandleUsernameLengthLimits(int usernameLength) throws Exception {
        // Given
        LoginRequestDto request = LoginRequestBuilder.aLoginRequest()
                .withUsername("a".repeat(usernameLength))
                .withPassword("validpass")
                .build();

        // When & Then
        if (usernameLength <= MAX_USERNAME_LENGTH) {
            when(authService.authenticate(any(LoginRequestDto.class)))
                    .thenThrow(new AuthenticationException("User not found"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } else {
            // Deve rejeitar por validação de tamanho
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"café", "naïve", "résumé"})
    @DisplayName("Should support simple international characters")
    void shouldSupportSimpleInternationalCharacters(String internationalUsername) throws Exception {
        // Given
        LoginRequestDto request = LoginRequestBuilder.aLoginRequest()
                .withUsername(internationalUsername)
                .withPassword("validpass")
                .build();

        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenThrow(new AuthenticationException("User not found"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).authenticate(argThat(req ->
                req.getUsername().equals(internationalUsername)
        ));
    }

    private static ResultMatcher anyOf(ResultMatcher... matchers) {
        return result -> {
            AssertionError lastError = null;
            for (ResultMatcher matcher : matchers) {
                try {
                    matcher.match(result);
                    return; // Sucesso
                } catch (AssertionError e) {
                    lastError = e;
                } catch (Exception e) {
                    lastError = new AssertionError("Matcher failed", e);
                }
            }
            // Nenhuma condição foi atendida
            throw new AssertionError(
                    "None of the " + matchers.length + " conditions were met. Last error: " +
                            (lastError != null ? lastError.getMessage() : "unknown"),
                    lastError
            );
        };
    }
}
