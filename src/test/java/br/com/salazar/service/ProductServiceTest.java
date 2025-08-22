package br.com.salazar.service;

import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductResponseDto;
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
    @Test
    void createProduct_Returns201AndBody() {
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

        ProductResponseDto body = new ProductResponseDto();
        body.setId(101L);
        ResponseEntity<ProductResponseDto> response =
                new ResponseEntity<>(body, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq("https://dummyjson.com/products/add"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ProductResponseDto.class)
        )).thenReturn(response);

        ProductResponseDto result = service.createProduct(req);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(101L);
    }

    @Test
    void createProduct_UnexpectedStatus_ThrowsRuntimeException() {
        ProductCreateRequestDto req = new ProductCreateRequestDto();
        req.setTitle("x"); req.setDescription("y"); req.setPrice(1.0);
        req.setDiscountPercentage(1.0); req.setRating(1.0);
        req.setStock(1); req.setBrand("b"); req.setCategory("c"); req.setThumbnail("t");

        ResponseEntity<ProductResponseDto> response =
                new ResponseEntity<>(new ProductResponseDto(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductResponseDto.class)))
                .thenReturn(response);

        assertThatThrownBy(() -> service.createProduct(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao criar produto");
    }

    @Test
    void getAllProducts_OK_ReturnsBody() {
        ProductsResponseDto body = new ProductsResponseDto();
        ResponseEntity<ProductsResponseDto> ok =
                new ResponseEntity<>(body, HttpStatus.OK);

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
    void getAllProducts_UnexpectedStatus_ThrowsRuntimeException() {
        ResponseEntity<ProductsResponseDto> resp =
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(anyString(), any(), any(), eq(ProductsResponseDto.class)))
                .thenReturn(resp);

        assertThatThrownBy(() -> service.getAllProducts())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao buscar produtos");
    }

    @Test
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
}

