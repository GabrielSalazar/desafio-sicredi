package br.com.salazar.testcases.service;

import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductResponseDto;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import br.com.salazar.service.ProductService.ForbiddenException;
import br.com.salazar.service.ProductService.UnauthorizedException;
import br.com.salazar.exception.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "baseUrl", "https://dummyjson.com");
    }

    @Test
    @DisplayName("getProducts with valid token returns body")
    void getProducts_OK_ReturnsBody() {
        ProductsResponseDto body = new ProductsResponseDto();
        ResponseEntity<ProductsResponseDto> ok = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/auth/products"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ProductsResponseDto.class)
        )).thenReturn(ok);

        ProductsResponseDto result = service.getProducts("validToken");

        assertThat(result).isNotNull();
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(ProductsResponseDto.class));
    }

    @Test
    @DisplayName("getProducts with invalid token throws UnauthorizedException")
    void getProducts_Unauthorized_ThrowsUnauthorizedException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized",
                HttpHeaders.EMPTY,
                "{\"name\":\"JsonWebTokenError\",\"message\":\"Invalid/Expired Token!\"}".getBytes(),
                null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> service.getProducts("badToken"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid/Expired Token");
    }

    @Test
    @DisplayName("getProducts with forbidden access throws ForbiddenException")
    void getProducts_Forbidden_ThrowsForbiddenException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN, "Forbidden",
                HttpHeaders.EMPTY,
                "{\"message\":\"Authentication Problem\"}".getBytes(),
                null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> service.getProducts("noPermission"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Authentication Problem");
    }

    @Test
    @DisplayName("createProduct returns ProductDto with converted data")
    void createProduct_Returns201AndConvertedDto() {
        // Given
        ProductCreateRequestDto req = new ProductCreateRequestDto();
        req.setTitle("Perfume Oil");
        req.setDescription("desc");
        req.setPrice(13.0);
        req.setDiscountPercentage(8.4);
        req.setRating(4.26);
        req.setStock(65);
        req.setBrand("brand");
        req.setCategory("fragrances");
        req.setThumbnail("thumb");

        ProductResponseDto apiResponse = new ProductResponseDto();
        apiResponse.setId(101L);
        apiResponse.setTitle("Perfume Oil");
        apiResponse.setPrice(13.0);
        apiResponse.setStock(65);

        ResponseEntity<ProductResponseDto> response = new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/products/add"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ProductResponseDto.class)
        )).thenReturn(response);

        // When
        ProductDto result = service.createProduct(req); // FIXED: Retorna ProductDto

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getName()).isEqualTo("Perfume Oil"); // Convertido title -> name
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("13.0")); // Convertido Double -> BigDecimal
        assertThat(result.getStockQuantity()).isEqualTo(65); // Convertido stock -> stockQuantity
    }

    @Test
    @DisplayName("createProduct with unexpected status throws RuntimeException")
    void createProduct_UnexpectedStatus_ThrowsRuntimeException() {
        ProductCreateRequestDto req = new ProductCreateRequestDto();
        req.setTitle("x");
        req.setDescription("y");
        req.setPrice(1.0);
        req.setDiscountPercentage(1.0);
        req.setRating(1.0);
        req.setStock(1);
        req.setBrand("b");
        req.setCategory("c");
        req.setThumbnail("t");

        ResponseEntity<ProductResponseDto> response = new ResponseEntity<>(new ProductResponseDto(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductResponseDto.class)))
                .thenReturn(response);

        assertThatThrownBy(() -> service.createProduct(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao criar produto");
    }

    @Test
    @DisplayName("getAllProducts returns products list")
    void getAllProducts_OK_ReturnsBody() {
        ProductsResponseDto body = new ProductsResponseDto();
        ResponseEntity<ProductsResponseDto> ok = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/products"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ProductsResponseDto.class)
        )).thenReturn(ok);

        ProductsResponseDto result = service.getAllProducts();

        assertThat(result).isNotNull();
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(ProductsResponseDto.class));
    }

    @Test
    @DisplayName("getAllProducts with error throws RuntimeException")
    void getAllProducts_UnexpectedStatus_ThrowsRuntimeException() {
        ResponseEntity<ProductsResponseDto> resp = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenReturn(resp);

        assertThatThrownBy(() -> service.getAllProducts())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao buscar produtos");
    }

    @Test
    @DisplayName("getProductById returns product")
    void getProductById_OK_ReturnsBody() {
        ProductDto body = new ProductDto();
        body.setId(1L);
        ResponseEntity<ProductDto> ok = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/products/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ProductDto.class)
        )).thenReturn(ok);

        ProductDto result = service.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getProductById with not found throws ProductNotFoundException")
    void getProductById_NotFound_ThrowsProductNotFoundException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found",
                HttpHeaders.EMPTY, "".getBytes(), null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductDto.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> service.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }
}
