package br.com.salazar.service;

import br.com.salazar.model.dto.*;
import br.com.salazar.exception.ProductNotFoundException;
import br.com.salazar.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Value("${app.dummyjson.base-url:https://dummyjson.com}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductDto createProduct(ProductCreateRequestDto request) {
        String url = baseUrl + "/products/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductCreateRequestDto> requestEntity = new HttpEntity<>(request, headers);

        try {
            // Chama a API externa que retorna ProductResponseDto
            ResponseEntity<ProductResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, ProductResponseDto.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                // CONVERSÃO: ProductResponseDto -> ProductDto
                return convertToProductDto(response.getBody());
            }
            throw new RuntimeException("Falha ao criar produto (status: " + response.getStatusCode() + ")");

        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP {} ao criar produto: {}", e.getStatusCode().value(), e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ValidationException("Dados de produto inválidos: " + extractMessage(e.getResponseBodyAsString(), "Invalid product data"));
            }
            throw new RuntimeException("Erro ao criar produto: " + e.getMessage());
        }
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

    public ProductsResponseDto getAllProducts() {
        String url = baseUrl + "/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductsResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, ProductsResponseDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new RuntimeException("Falha ao buscar produtos (status: " + response.getStatusCode() + ")");
        } catch (HttpClientErrorException e) {
            log.error("Erro ao buscar produtos: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar produtos", e);
        }
    }

    public ProductDto getProductById(Long id) {
        String url = baseUrl + "/products/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, ProductDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new ProductNotFoundException("Produto não encontrado com id: " + id);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException("Product not found with id: " + id);
            }
            log.error("Erro ao buscar produto {}: {}", id, e.getMessage());
            throw new RuntimeException("Erro ao buscar produto", e);
        }
    }

    // MÉTODO AUXILIAR: Converter ProductResponseDto para ProductDto
    private ProductDto convertToProductDto(ProductResponseDto responseDto) {
        ProductDto dto = new ProductDto();
        dto.setId(responseDto.getId());
        dto.setName(responseDto.getTitle()); // Mapeamento: title -> name
        dto.setDescription(responseDto.getDescription());

        // Conversão de Double para BigDecimal
        if (responseDto.getPrice() != null) {
            dto.setPrice(BigDecimal.valueOf(responseDto.getPrice()));
        }

        dto.setBrand(responseDto.getBrand());
        dto.setCategory(responseDto.getCategory());
        dto.setStockQuantity(responseDto.getStock()); // Mapeamento: stock -> stockQuantity

        return dto;
    }

    private ProductResponseDto convertToProductResponseDto(ProductCreateRequestDto request) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setPrice(request.getPrice());
        dto.setDiscountPercentage(request.getDiscountPercentage());
        dto.setRating(request.getRating());
        dto.setStock(request.getStock());
        dto.setBrand(request.getBrand());
        dto.setCategory(request.getCategory());
        dto.setThumbnail(request.getThumbnail());
        return dto;
    }

    private String extractMessage(String body, String defaultMsg) {
        try {
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
