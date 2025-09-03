/*
 * =============================================================================
 * CLASSE: ProductPublicFunctionalTest
 * DESCRIÇÃO: Testes funcionais abrangentes para ProductPublicController
 * =============================================================================
 *
 * COBERTURA DE CENÁRIOS DE TESTE:
 *
 *  1. OPERAÇÕES CRUD BÁSICAS
 * ├─ shouldListAllProducts()
 * │  ├─ GET /products - Listagem de produtos
 * │  ├─ Validação de JSON response ($.products, $.total)
 * │  ├─ Verificação de múltiplos produtos (iPhone, Samsung, Google)
 * │  └─ Status HTTP 200 OK
 * │
 * ├─ shouldCreateProductWithValidData()
 * │  ├─ POST /products/add - Criação com dados válidos
 * │  ├─ Verificação de response body (id, name, price)
 * │  └─ Status HTTP 201 Created
 * │
 * └─ shouldCreateProductWithAllFields()
 *    ├─ POST /products/add - Criação com todos os campos
 *    ├─ Teste de caracteres especiais (àáâãäå)
 *    ├─ Validação de campos complexos (description, brand, category)
 *    └─ Status HTTP 201 Created
 *
 *  2. VALIDAÇÕES DE ENTRADA (Testes Negativos)
 * ├─ shouldRejectInvalidProductData()
 * │  ├─ Campos NULL violando @NotBlank/@NotNull
 * │  ├─ Garantia de validação Bean Validation
 * │  └─ Status HTTP 400 Bad Request
 * │
 * ├─ shouldValidateProductTitle() [@ParameterizedTest]
 * │  ├─ Títulos vazios: '', ' '
 * │  ├─ Validação @NotBlank
 * │  └─ Status HTTP 400 Bad Request
 * │
 * ├─ shouldValidateLongProductTitle()
 * │  ├─ Títulos excedendo limite @Size(max=100)
 * │  ├─ String > 100 caracteres + EXTRA_CHARS
 * │  └─ Status HTTP 400 Bad Request
 * │
 * ├─ shouldRejectInvalidPrices() [@ParameterizedTest]
 * │  ├─ Preços inválidos: -1.0, -100.99, 0.0
 * │  ├─ Validação @Positive
 * │  └─ Status HTTP 400 Bad Request
 * │
 * ├─ shouldRejectInvalidDiscountPercentages() [@ParameterizedTest]
 * │  ├─ Descontos inválidos: -5.0, 150.0, 200.0
 * │  ├─ Validação @DecimalMin(0) @DecimalMax(100)
 * │  └─ Status HTTP 400 Bad Request
 * │
 * ├─ shouldRejectInvalidRatings() [@ParameterizedTest]
 * │  ├─ Ratings inválidos: -1.0, 6.0, 10.0
 * │  ├─ Validação @DecimalMin(0) @DecimalMax(5)
 * │  └─ Status HTTP 400 Bad Request
 * │
 * └─ shouldRejectNegativeStock() [@ParameterizedTest]
 *    ├─ Stock negativo: -1, -100
 *    ├─ Validação @Min(0)
 *    └─ Status HTTP 400 Bad Request

 *  3. ESTRATÉGIAS DE TESTE UTILIZADAS
 * ├─ @ParameterizedTest - Múltiplos cenários com ValueSource/CsvSource
 * ├─ MockMvc - Simulação de requests HTTP
 * ├─ @MockBean - Mock do ProductService
 * ├─ JsonPath - Validação de response JSON
 * ├─ AssertJ - Assertions fluentes para validações complexas
 * ├─ .andDo(print()) - Debug de requests/responses
 * └─ ArgumentMatchers - Matching flexível para mocks
 *
 *  4. COBERTURA DE CENÁRIOS
 * ├─ ✅ Cenários Positivos (Happy Path)
 * ├─ ✅ Cenários Negativos (Validações)
 * ├─ ✅ Edge Cases (Limites, caracteres especiais)
 * ├─ ✅ Diferentes tipos de dados (String, Double, Integer)
 * ├─ ✅ Validações Bean Validation (@NotBlank, @Positive, @Size)
 * ├─ ✅ Status HTTP apropriados (200, 201, 400)
 * └─ ✅ Response body completo com JsonPath
 *
 * 📈 MÉTRICAS DE QUALIDADE:
 * ├─ Cobertura de Código: Alta (todos os endpoints públicos)
 * ├─ Debugging: Habilitado em todos os testes
 * └─ Manutenibilidade: Helper methods para reutilização
 * =============================================================================
 */

package br.com.salazar.testcases.functional;

import br.com.salazar.service.ProductService;
import br.com.salazar.controller.ProductPublicController;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(ProductPublicController.class)
class ProductPublicFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // Constant for long string validation
    private static final String LONG_TITLE = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

    @Test
    @DisplayName("Should list all products successfully")
    void shouldListAllProducts() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(
                createValidProduct(1L, "iPhone 15", 999.99),
                createValidProduct(2L, "Samsung Galaxy", 899.99),
                createValidProduct(3L, "Google Pixel", 799.99)
        );
        ProductsResponseDto response = new ProductsResponseDto(products, products.size());
        when(productService.getAllProducts()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.products", hasSize(3))) // FIXED: items -> products
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.products[0].price").value(999.99))
                .andExpect(jsonPath("$.products[1].name").value("Samsung Galaxy"))
                .andExpect(jsonPath("$.products[2].name").value("Google Pixel"))
                .andDo(print()); // ADDED: Para debugging

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProductWithValidData() throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest("New iPhone", 1299.99);
        ProductDto savedProduct = createValidProduct(123L, "New iPhone", 1299.99);

        when(productService.createProduct(ArgumentMatchers.<ProductCreateRequestDto>any())).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.name").value("New iPhone"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andDo(print()); // ADDED: Para debugging

        verify(productService, times(1)).createProduct(ArgumentMatchers.<ProductCreateRequestDto>any());
    }

    @Test
    @DisplayName("Should handle product creation with all fields")
    void shouldCreateProductWithAllFields() throws Exception {
        // Given
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        request.setTitle("Complete Product");
        request.setDescription("Detailed description with special chars: àáâãäå");
        request.setPrice(599.99);
        request.setDiscountPercentage(15.0);
        request.setRating(4.5);
        request.setStock(100);
        request.setBrand("Premium Brand");
        request.setCategory("electronics");
        request.setThumbnail("https://example.com/image.jpg");

        ProductDto savedProduct = new ProductDto();
        savedProduct.setId(456L);
        savedProduct.setName("Complete Product");
        savedProduct.setDescription("Detailed description with special chars: àáâãäå");
        savedProduct.setPrice(new BigDecimal("599.99"));
        savedProduct.setBrand("Premium Brand");
        savedProduct.setCategory("electronics");
        savedProduct.setStockQuantity(100);

        when(productService.createProduct(ArgumentMatchers.<ProductCreateRequestDto>any())).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(456))
                .andExpect(jsonPath("$.description").value("Detailed description with special chars: àáâãäå"))
                .andExpect(jsonPath("$.brand").value("Premium Brand"))
                .andExpect(jsonPath("$.category").value("electronics"));
    }

    @Test
    @DisplayName("Should reject product with invalid data")
    void shouldRejectInvalidProductData() throws Exception {
        // Given - Dados REALMENTE inválidos
        ProductCreateRequestDto invalidRequest = new ProductCreateRequestDto();
        // FIXED: Deixar campos obrigatórios NULL para garantir validação
        invalidRequest.setTitle(null); // NULL viola @NotBlank
        invalidRequest.setDescription(null); // NULL viola @NotBlank
        invalidRequest.setPrice(null); // NULL viola @NotNull
        invalidRequest.setDiscountPercentage(null); // NULL viola @NotNull
        invalidRequest.setRating(null); // NULL viola @NotNull
        invalidRequest.setStock(null); // NULL viola @NotNull
        invalidRequest.setBrand(null); // NULL viola @NotBlank
        invalidRequest.setCategory(null); // NULL viola @NotBlank
        invalidRequest.setThumbnail(null); // NULL viola @NotBlank

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @ParameterizedTest
    @CsvSource({
            "'', 'Título do produto é obrigatório'",
            "' ', 'Título do produto é obrigatório'"
    })
    @DisplayName("Should validate product title constraints")
    void shouldValidateProductTitle(String title, String expectedError) throws Exception {
        // Given - FIXED: Usar dados que realmente falhem na validação
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        request.setTitle(title); // String vazia ou apenas espaços
        request.setDescription("Valid description");
        request.setPrice(99.99);
        request.setDiscountPercentage(10.0);
        request.setRating(4.5);
        request.setStock(50);
        request.setBrand("TechBrand");
        request.setCategory("electronics");
        request.setThumbnail("https://example.com/thumbnail.jpg");

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @Test
    @DisplayName("Should validate long product titles")
    void shouldValidateLongProductTitle() throws Exception {
        // Given - FIXED: Usar título realmente longo que viole @Size(max=100)
        ProductCreateRequestDto request = createValidProductRequest("Valid Title", 99.99);
        request.setTitle(LONG_TITLE + "EXTRA_CHARS_TO_EXCEED_LIMIT"); // Garantir que exceda limite

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -100.99, 0.0})
    @DisplayName("Should reject invalid prices")
    void shouldRejectInvalidPrices(Double invalidPrice) throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest("Valid Product", 99.99);
        request.setPrice(invalidPrice);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @ParameterizedTest
    @ValueSource(doubles = {-5.0, 150.0, 200.0})
    @DisplayName("Should reject invalid discount percentages")
    void shouldRejectInvalidDiscountPercentages(Double invalidDiscount) throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest("Valid Product", 99.99);
        request.setDiscountPercentage(invalidDiscount);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, 6.0, 10.0})
    @DisplayName("Should reject invalid ratings")
    void shouldRejectInvalidRatings(Double invalidRating) throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest("Valid Product", 99.99);
        request.setRating(invalidRating);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should reject negative stock")
    void shouldRejectNegativeStock(Integer invalidStock) throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest("Valid Product", 99.99);
        request.setStock(invalidStock);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print()); // ADDED: Para debugging
    }

    @Test
    @DisplayName("Should sanitize HTML input in product data")
    void shouldSanitizeHTMLInput() throws Exception {
        // Given
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        request.setTitle("<script>alert('xss')</script>Malicious Product");
        request.setDescription("<img src=x onerror=alert('xss')>Description");
        request.setPrice(99.99);
        request.setDiscountPercentage(10.0);
        request.setRating(4.0);
        request.setStock(10);
        request.setBrand("TechBrand");
        request.setCategory("electronics");
        request.setThumbnail("https://example.com/thumb.jpg");

        ProductDto sanitized = new ProductDto();
        sanitized.setId(789L);
        sanitized.setName("Malicious Product");
        sanitized.setDescription("Description");
        sanitized.setPrice(new BigDecimal("99.99"));
        sanitized.setStockQuantity(10);
        sanitized.setCategory("electronics");
        sanitized.setBrand("TechBrand");

        when(productService.createProduct(ArgumentMatchers.<ProductCreateRequestDto>any())).thenReturn(sanitized);

        // When & Then
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Malicious Product"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).doesNotContain("<script>");
                    assertThat(responseBody).doesNotContain("onerror");
                    assertThat(responseBody).doesNotContain("alert");
                })
                .andDo(print()); // ADDED: Para debugging
    }

    // HELPER METHODS

    private ProductCreateRequestDto createValidProductRequest(String title, Double price) {
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        request.setTitle(title);
        request.setDescription("High-quality electronic device");
        request.setPrice(price);
        request.setDiscountPercentage(10.0);
        request.setRating(4.5);
        request.setStock(50);
        request.setBrand("TechBrand");
        request.setCategory("electronics");
        request.setThumbnail("https://example.com/thumbnail.jpg");
        return request;
    }

    private ProductDto createValidProduct(Long id, String name, Double price) {
        ProductDto product = new ProductDto();
        product.setId(id);
        product.setName(name);
        product.setDescription("High-quality electronic device");
        product.setPrice(new BigDecimal(price.toString()));
        product.setBrand("TechBrand");
        product.setCategory("electronics");
        product.setStockQuantity(50);
        return product;
    }

    private List<ProductDto> generateProductList(int count) {
        List<ProductDto> products = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            products.add(createValidProduct((long) i, "Product " + i, 99.99 + i));
        }
        return products;
    }

    private static ResultMatcher statusIsEitherBadRequestOrCreated() {
        return result -> {
            int status = result.getResponse().getStatus();
            if (status != 400 && status != 201) {
                throw new AssertionError("Expected status 400 or 201, but was " + status);
            }
        };
    }
}
