package br.com.salazar.controller;

import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductPublicController.class)
class ProductPublicControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper om;

    @Test
    void getAllProducts_Returns200() throws Exception {
        ProductsResponseDto dto = new ProductsResponseDto();
        when(productService.getAllProducts()).thenReturn(dto);

        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_Returns200() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setTitle("Essence Mascara Lash Princess");

        when(productService.getProductById(1L)).thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Essence Mascara Lash Princess"));

        verify(productService, times(1)).getProductById(1L);
    }
}

