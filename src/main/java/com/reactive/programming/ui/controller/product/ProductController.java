package com.reactive.programming.ui.controller.product;

import com.reactive.programming.service.product.ProductService;
import com.reactive.programming.shared.product.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("create")
    public Mono<ResponseEntity<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping("/get/{productId}")
    public Mono<ResponseEntity<ProductDTO>> getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/list")
    public Flux<ProductDTO> getAllProduct() {
        return productService.getAllProducts();
    }

    @PutMapping("/update/{productId}")
    public Mono<ResponseEntity<ProductDTO>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDTO ProductDTO) {
        return productService.updateProduct(productId, ProductDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{productId}")
    public Mono<ResponseEntity<Map<String, String>>> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId)
                .then(Mono.just(ResponseEntity.ok(
                        Map.of("message", "Product deleted successfully",
                                "productId", productId.toString())
                )))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()))));
    }

}
