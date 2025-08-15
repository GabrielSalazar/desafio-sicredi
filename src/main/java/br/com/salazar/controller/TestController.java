package br.com.salazar.controller;

import br.com.salazar.model.dto.TestResponseDto;
import br.com.salazar.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public ResponseEntity<TestResponseDto> test() {
        return ResponseEntity.ok(testService.getTestStatus());
    }
}
