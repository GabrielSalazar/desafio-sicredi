package br.com.salazar.testcases.controller;

import br.com.salazar.controller.ProductController;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductDto; // FIXED: ProductResponseDto -> ProductDto
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import br.com.salazar.service.ProductService.ForbiddenException;
import br.com.salazar.service.ProductService.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    ObjectMapper om = new ObjectMapper();

    @Test
    @DisplayName("getProducts with valid bearer token returns 200")
    void getProducts_WithValidBearer_Returns200() throws Exception {
        ProductsResponseDto dto = new ProductsResponseDto();
        when(productService.getProducts(any(String.class))).thenReturn(dto);

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer token123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getProducts without Authorization header returns 401")
    void getProducts_WithoutAuthorizationHeader_Returns401() throws Exception {
        mockMvc.perform(get("/auth/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Missing or invalid Authorization header"));
    }

    @Test
    @DisplayName("getProducts throws UnauthorizedException returns 401")
    void getProducts_ThrowsUnauthorizedException_Returns401() throws Exception {
        when(productService.getProducts(any(String.class)))
                .thenThrow(new UnauthorizedException("Invalid/Expired Token!"));

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer bad")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid/Expired Token!"));
    }

    @Test
    @DisplayName("getProducts throws ForbiddenException returns 403")
    void getProducts_ThrowsForbiddenException_Returns403() throws Exception {
        when(productService.getProducts(any(String.class)))
                .thenThrow(new ForbiddenException("Authentication Problem"));

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer no")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Authentication Problem"));
    }

    @Test
    @DisplayName("post add product with valid data returns 201")
    void postAddProduct_WithValidData_Returns201() throws Exception {
        // FIXED: Usar ProductDto em vez de ProductResponseDto
        ProductDto resp = new ProductDto();
        resp.setId(101L);
        resp.setName("Perfume Oil"); // name em vez de title
        resp.setPrice(new BigDecimal("13.0")); // BigDecimal em vez de Double
        resp.setStockQuantity(65); // stockQuantity em vez de stock
        resp.setDescription("Mega Discount, Impression A...");
        resp.setBrand("Impression of Acqua Di Gio");
        resp.setCategory("fragrances");

        when(productService.createProduct(any())).thenReturn(resp);

        ProductCreateRequestDto req = new ProductCreateRequestDto();
        req.setTitle("Perfume Oil");
        req.setDescription("Mega Discount, Impression A...");
        req.setPrice(13.0);
        req.setDiscountPercentage(8.4);
        req.setRating(4.26);
        req.setStock(65);
        req.setBrand("Impression of Acqua Di Gio");
        req.setCategory("fragrances");
        req.setThumbnail("https://i.dummyjson.com/data/products/11/thumnail.jpg");

        mockMvc.perform(post("/auth/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101));
    }

    @Test
    @DisplayName("post add product with invalid data returns 400")
    void postAddProduct_WithInvalidData_Returns400() throws Exception {
        String invalidJson = "{\"description\":\"desc\",\"brand\":\"brand\",\"category\":\"cat\",\"thumbnail\":\"thumb\"}";

        mockMvc.perform(post("/auth/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
