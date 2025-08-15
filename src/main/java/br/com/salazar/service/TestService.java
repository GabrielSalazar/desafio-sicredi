package br.com.salazar.service;

import br.com.salazar.model.dto.TestResponseDto;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public TestResponseDto getTestStatus() {
        return new TestResponseDto("ok", "GET");
    }
}

