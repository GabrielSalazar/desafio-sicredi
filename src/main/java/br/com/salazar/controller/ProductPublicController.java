package br.com.salazar.controller;

import br.com.salazar.model.dto.ProductDto;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductPublicController {

    private final ProductService service;

    public ProductPublicController(ProductService service) {
        this.service = service;
    }

    // GET /products (público)
    @GetMapping
    public ResponseEntity<ProductsResponseDto> getAllProducts() {
        ProductsResponseDto products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = service.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // ADDED: POST /products/add (público) - para os testes funcionarem
    @PostMapping("/add")
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductCreateRequestDto request) {

        ProductDto created = service.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
