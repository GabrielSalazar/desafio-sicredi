package br.com.salazar.service;

import br.com.salazar.model.dto.LoginRequestDto;
import br.com.salazar.model.dto.LoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${app.dummyjson.base-url:https://dummyjson.com}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginResponseDto authenticate(LoginRequestDto loginRequest) {
        String url = baseUrl + "/auth/login";

        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar a requisição
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginRequest, headers);

        try {
            logger.info("Tentando autenticar usuário: {}", loginRequest.getUsername());

            // Fazer a requisição para a API externa
            ResponseEntity<LoginResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    LoginResponseDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Autenticação bem-sucedida para usuário: {}", loginRequest.getUsername());
                return response.getBody();
            } else {
                logger.warn("Resposta inesperada da API de autenticação");
                throw new RuntimeException("Falha na autenticação");
            }

        } catch (Exception e) {
            logger.error("Erro durante a autenticação do usuário {}: {}",
                    loginRequest.getUsername(), e.getMessage());
            throw new RuntimeException("Credenciais inválidas", e);
        }
    }
}
