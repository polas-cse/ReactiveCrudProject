package com.reactive.programming.service.product;

import com.reactive.programming.shared.product.ProductDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService{

    @Override
    public Mono<ProductDTO> createProduct(ProductDTO productDTO) {
        return null;
    }

    @Override
    public Mono<ProductDTO> getProductById(String id) {
        return null;
    }

    @Override
    public Flux<ProductDTO> getAllProducts() {
        return null;
    }

    @Override
    public Mono<ProductDTO> updateProduct(String id, ProductDTO productDTO) {
        return null;
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return null;
    }

    @Override
    public Flux<ProductDTO> getProductsByCategory(String category) {
        return null;
    }

}
