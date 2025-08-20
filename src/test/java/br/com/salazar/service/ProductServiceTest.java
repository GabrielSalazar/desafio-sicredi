package br.com.salazar.service;

import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService.ForbiddenException;
import br.com.salazar.service.ProductService.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
    void getProducts_OK_ReturnsBody() {
        ProductsResponseDto body = new ProductsResponseDto();
        ResponseEntity<ProductsResponseDto> ok =
                new ResponseEntity<>(body, HttpStatus.OK);

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
    void getProducts_Unauthorized_ThrowsUnauthorizedException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized",
                HttpHeaders.EMPTY, "{\"name\":\"JsonWebTokenError\",\"message\":\"Invalid/Expired Token!\"}".getBytes(), null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> service.getProducts("badToken"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid/Expired Token");
    }

    @Test
    void getProducts_Forbidden_ThrowsForbiddenException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN, "Forbidden",
                HttpHeaders.EMPTY, "{\"message\":\"Authentication Problem\"}".getBytes(), null);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> service.getProducts("noPermission"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Authentication Problem");
    }
}

