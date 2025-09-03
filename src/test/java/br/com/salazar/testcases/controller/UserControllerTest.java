package br.com.salazar.testcases.controller;

import br.com.salazar.controller.UserController;
import br.com.salazar.model.dto.UserDto;
import br.com.salazar.model.dto.UsersResponseDto;
import br.com.salazar.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return list of users")
    void deveRetornarListaDeUsuarios() {
        // Arrange
        UsersResponseDto mockResponse = new UsersResponseDto();
        UserDto user = new UserDto();
        user.setId(1L);
        user.setFirstName("Terry");
        user.setLastName("Medhurst");
        user.setEmail("atuny0@sohu.com");
        mockResponse.setUsers(Arrays.asList(user));

        when(userService.getUsers()).thenReturn(mockResponse);

        // Act
        ResponseEntity<UsersResponseDto> response = userController.getUsers(); // FIXED: Added generic

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getUsers().size());
        assertEquals("Terry", response.getBody().getUsers().get(0).getFirstName());
    }
}
