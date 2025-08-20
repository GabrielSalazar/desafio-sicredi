package br.com.salazar.controller;

import br.com.salazar.model.dto.UsersResponseDto;
import br.com.salazar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UsersResponseDto> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }
}