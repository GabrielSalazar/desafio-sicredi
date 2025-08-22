package br.com.salazar.controller;
import br.com.salazar.model.dto.ProductCreateRequestDto;
import br.com.salazar.model.dto.ProductResponseDto;
import br.com.salazar.model.dto.ProductsResponseDto;
import br.com.salazar.service.ProductService;
import br.com.salazar.service.ProductService.ForbiddenException;
import br.com.salazar.service.ProductService.UnauthorizedException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public ResponseEntity<ProductsResponseDto> getProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // lança UnauthorizedException se faltar/for inválido
        String token = extractBearer(authorizationHeader);
        ProductsResponseDto products = service.getProducts(token);
        return ResponseEntity.ok(products);
    }
    @PostMapping("/products/add")
    public ResponseEntity<ProductResponseDto> addProduct(
            @Valid @RequestBody ProductCreateRequestDto payload) {

        ProductResponseDto created = service.createProduct(payload);
        // A API externa retorna 201, então espelhamos
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        return header.substring("Bearer ".length()).trim();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}

