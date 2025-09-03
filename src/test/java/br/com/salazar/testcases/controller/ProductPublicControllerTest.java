package br.com.salazar.testcases.controller;

import br.com.salazar.controller.ProductPublicController;
import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
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

@WebMvcTest(ProductPublicController.class)
class ProductPublicControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService; // FIXED: Corrigido nome do service

    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("getAllProducts returns 200 OK")
    void getAllProducts_Returns200() throws Exception {
        ProductsResponseDto dto = new ProductsResponseDto();
        when(productService.getAllProducts()).thenReturn(dto);

        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("getProductById returns 200 OK with product data")
    void getProductById_Returns200() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setName("Essence Mascara Lash Princess"); // FIXED: name em vez de title
        dto.setPrice(new BigDecimal("12.99")); // FIXED: BigDecimal

        when(productService.getProductById(1L)).thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Essence Mascara Lash Princess")); // name, não title

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("createProduct returns 201 Created")
    void createProduct_Returns201() throws Exception {
        // Given
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        request.setTitle("New Product");
        request.setDescription("A great product");
        request.setPrice(99.99);
        request.setDiscountPercentage(10.0);
        request.setRating(4.5);
        request.setStock(50);
        request.setBrand("TestBrand");
        request.setCategory("electronics");
        request.setThumbnail("https://example.com/image.jpg");

        ProductDto responseDto = new ProductDto();
        responseDto.setId(123L);
        responseDto.setName("New Product");
        responseDto.setDescription("A great product");
        responseDto.setPrice(new BigDecimal("99.99"));
        responseDto.setBrand("TestBrand");
        responseDto.setCategory("electronics");
        responseDto.setStockQuantity(50);

        when(productService.createProduct(any(ProductCreateRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.name").value("New Product"));

        verify(productService, times(1)).createProduct(any(ProductCreateRequestDto.class));
    }
}
