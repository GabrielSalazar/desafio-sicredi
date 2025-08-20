package br.com.salazar.controller;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import br.com.salazar.service.ProductService.ForbiddenException;
import br.com.salazar.service.ProductService.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    ObjectMapper om = new ObjectMapper();

    @Test
    void getProducts_WithValidBearer_Returns200() throws Exception {
        ProductsResponseDto dto = new ProductsResponseDto();
        when(productService.getProducts(anyString())).thenReturn(dto);

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_MissingHeader_Returns401() throws Exception {
        mockMvc.perform(get("/auth/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Missing or invalid Authorization header"));
    }

    @Test
    void getProducts_ServiceThrowsUnauthorized_Returns401() throws Exception {
        when(productService.getProducts(anyString()))
                .thenThrow(new UnauthorizedException("Invalid/Expired Token!"));

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer bad"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid/Expired Token!"));
    }

    @Test
    void getProducts_ServiceThrowsForbidden_Returns403() throws Exception {
        when(productService.getProducts(anyString()))
                .thenThrow(new ForbiddenException("Authentication Problem"));

        mockMvc.perform(get("/auth/products")
                        .header("Authorization", "Bearer noPerm"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Authentication Problem"));
    }
}
