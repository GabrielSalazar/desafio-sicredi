package br.com.salazar.service;
import br.com.salazar.model.dto.*;
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

    public ProductResponseDto createProduct(ProductCreateRequestDto body) {
        String url = baseUrl + "/products/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductCreateRequestDto> request = new HttpEntity<>(body, headers);

        ResponseEntity<ProductResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, request, ProductResponseDto.class);

        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
            return response.getBody();
        }
        // A API retorna 201; se vier 200/qualquer outro, trate como inesperado
        throw new RuntimeException("Falha ao criar produto (status: " + response.getStatusCode() + ")");
    }

    public ProductsResponseDto getAllProducts() {
        String url = baseUrl + "/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<ProductsResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, request, ProductsResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Falha ao buscar produtos (status: " + response.getStatusCode() + ")");
    }

    public ProductDto getProductById(Long id) {
        String url = baseUrl + "/products/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(
                url, HttpMethod.GET, request, ProductDto.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Falha ao buscar produto id=" + id + " (status: " + response.getStatusCode() + ")");
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
