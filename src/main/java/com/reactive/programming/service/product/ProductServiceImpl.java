package com.reactive.programming.service.product;

import com.reactive.programming.data.product.ProductDAO;

import com.reactive.programming.data.product.entity.ProductEntity;
import com.reactive.programming.shared.product.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductDAO productDao;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductDAO productDao, ModelMapper modelMapper) {
        this.productDao = productDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public Mono<ProductDTO> createProduct(ProductDTO productDTO) {
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);

        return productDao.createProduct(productEntity)
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .doOnSubscribe(res -> log.info("Creating product with product name: {}", productEntity.getName()))
                .doOnSuccess(dto -> log.info("product created: {}", dto))
                .doOnError(err -> log.error("Failed to create product: {}", err.getMessage()));
    }

    @Override
    public Mono<ProductDTO> getProductById(Long productId) {
        log.info("Fetching user by ID: {}", productId);

        return productDao.getProductById(productId)
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .doOnSubscribe(res-> log.info("Product found with product id: {}", productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with product id:" + productId)));
    }

    @Override
    public Flux<ProductDTO> getAllProducts() {
        log.info("Fetching all product");

        return productDao.getAllProducts()
                .map(product-> modelMapper.map(product, ProductDTO.class))
                .doOnComplete(()-> log.info("All product fetched"));
    }

    @Override
    public Mono<ProductDTO> updateProduct(Long productId, ProductDTO productDTO) {
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);

        return productDao.updateProduct(productId, productEntity)
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .doOnSubscribe(res -> log.info("Update product with name: {}", productEntity.getName()))
                .doOnSuccess(dto -> log.info("Product updated: {}", dto))
                .doOnError(err -> log.error("Failed to updated product: {}", err.getMessage()));
    }

    @Override
    public Mono<Void> deleteProduct(Long productId) {
        log.info("Deleting product with product ID: {}", productId);
        return productDao.deleteProduct(productId)
                .flatMap(rows -> {
                    if (rows > 0) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException("Product not found with ID: " + productId));
                    }
                });
    }

}
