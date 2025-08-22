package br.com.salazar.controller;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductResponseDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Test
    void addProduct_ValidBody_Returns201() throws Exception {
        ProductResponseDto resp = new ProductResponseDto();
        resp.setId(101L);
        resp.setTitle("Perfume Oil");
        resp.setPrice(13.0);
        resp.setStock(65);
        resp.setRating(4.26);
        resp.setThumbnail("https://i.dummyjson.com/data/products/11/thumnail.jpg");
        resp.setDescription("Mega Discount, Impression of A...");
        resp.setBrand("Impression of Acqua Di Gio");
        resp.setCategory("fragrances");

        when(productService.createProduct(any())).thenReturn(resp);

        ProductCreateRequestDto req = new ProductCreateRequestDto();
        req.setTitle("Perfume Oil");
        req.setDescription("Mega Discount, Impression of A...");
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
    void addProduct_InvalidBody_Returns400() throws Exception {
        String invalidJson = """
        {"description":"desc","brand":"brand","category":"cat","thumbnail":"thumb"}
        """;

        mockMvc.perform(post("/auth/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
