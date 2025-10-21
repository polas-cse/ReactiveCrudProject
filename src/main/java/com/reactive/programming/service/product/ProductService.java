package com.reactive.programming.service.product;

import com.reactive.programming.shared.product.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<ProductDTO> createProduct(ProductDTO productDTO);
    Mono<ProductDTO> getProductById(String id);
    Flux<ProductDTO> getAllProducts();
    Mono<ProductDTO> updateProduct(String id, ProductDTO productDTO);
    Mono<Void> deleteProduct(String id);
    Flux<ProductDTO> getProductsByCategory(String category);

}
