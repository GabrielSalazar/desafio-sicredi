package br.com.salazar.service;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.model.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Value("${app.dummyjson.base-url:https://dummyjson.com}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductsResponseDto getProducts(String bearerToken) {
        String url = baseUrl + "/auth/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductsResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, ProductsResponseDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new RuntimeException("Resposta inesperada da API de produtos");
        } catch (HttpClientErrorException e) {
            // Mapeia mensagens 401/403 vindas do DummyJSON
            String body = e.getResponseBodyAsString();
            log.warn("Erro HTTP {} ao buscar produtos: {}", e.getStatusCode().value(), body);

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException(extractMessage(body, "Invalid/Expired Token!"));
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ForbiddenException(extractMessage(body, "Authentication Problem"));
            }
            throw e;
        }
    }

    private String extractMessage(String body, String defaultMsg) {
        try {
            // parse simples sem ObjectMapper, para manter o serviço independente
            if (body != null && body.contains("message")) {
                int idx = body.indexOf("\"message\"");
                if (idx >= 0) {
                    int start = body.indexOf(':', idx) + 1;
                    int end = body.indexOf('}', start);
                    if (start > 0 && end > start) {
                        String raw = body.substring(start, end).trim();
                        return raw.replace("\"", "");
                    }
                }
            }
        } catch (Exception ignored) {}
        return defaultMsg;
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String msg) { super(msg); }
    }
}
