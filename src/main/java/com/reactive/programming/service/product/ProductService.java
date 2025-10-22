package com.reactive.programming.service.product;

import com.reactive.programming.shared.product.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<ProductDTO> createProduct(ProductDTO productDTO);
    Mono<ProductDTO> getProductById(Long productId);
    Flux<ProductDTO> getAllProducts();
    Mono<ProductDTO> updateProduct(Long productId, ProductDTO productDTO);
    Mono<Void> deleteProduct(Long productId);

}
