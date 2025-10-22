package com.reactive.programming.data.product;

import com.reactive.programming.data.product.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class ProductDAO {

    private final DatabaseClient databaseClient;

    @Autowired
    public ProductDAO(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<ProductEntity> createProduct(ProductEntity productEntity) {
        log.info("Creating product: {}", productEntity.getName());

        String sql = " INSERT INTO products (name, description, price, quantity, category, active, created_at, updated_at) " +
                " VALUES (:name, :description, :price, :quantity, :category, :active, :createdAt, :updatedAt) " +
                " RETURNING product_id, name, description, price, quantity, category, active, created_at, updated_at";

        return databaseClient.sql(sql)
                .bind("name", productEntity.getName())
                .bind("description", productEntity.getDescription())
                .bind("price", productEntity.getPrice())
                .bind("quantity", productEntity.getQuantity())
                .bind("category", productEntity.getCategory())
                .bind("active", productEntity.getActive() != null ? productEntity.getActive() : true)
                .bind("createdAt", LocalDateTime.now())
                .bind("updatedAt", LocalDateTime.now())
                .map((row, meta) -> {
                    ProductEntity product = new ProductEntity();
                    product.setProductId(row.get("product_id", Long.class));
                    product.setName(row.get("name", String.class));
                    product.setDescription(row.get("description", String.class));

                    Double price = row.get("price", Double.class);
                    product.setPrice(price != null ? price : 0.0);

                    Integer quantity = row.get("quantity", Integer.class);
                    product.setQuantity(quantity != null ? quantity : 0);

                    product.setCategory(row.get("category", String.class));
                    product.setActive(row.get("active", Boolean.class));
                    product.setCreatedAt(row.get("created_at", LocalDateTime.class));
                    product.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
                    return product;
                })
                .one()
                .doOnSuccess(p -> log.info("product created successfully with ID: {}", p.getProductId()))
                .doOnError(err -> log.error("Error creating product: {}", err.getMessage()));
    }

    public Mono<ProductEntity> getProductById(Long productId) {
        log.info("Fetching product by ID: {}", productId);

        return databaseClient.sql(
                        "SELECT product_id, name, description, price, quantity, category, active, created_at, updated_at " +
                                "FROM products WHERE product_id = :productId"
                ).bind("productId", productId)
                .map((row, metadata) -> {
                    ProductEntity product = new ProductEntity();
                    product.setProductId(row.get("product_id", Long.class));
                    product.setName(row.get("name", String.class));
                    product.setDescription(row.get("description", String.class));

                    Double price = row.get("price", Double.class);
                    product.setPrice(price != null ? price : 0.0);

                    Integer quantity = row.get("quantity", Integer.class);
                    product.setQuantity(quantity != null ? quantity : 0);

                    product.setCategory(row.get("category", String.class));
                    product.setActive(row.get("active", Boolean.class));
                    product.setCreatedAt(row.get("created_at", LocalDateTime.class));
                    product.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
                    return product;
                })
                .one()
                .doOnSuccess(entity -> log.info("product found with productId: {}", entity.getProductId()))
                .switchIfEmpty(Mono.error(new RuntimeException("product not found with ID: " + productId)));
    }


    public Flux<ProductEntity> getAllProducts() {
        log.info("Fetching all products");

        return databaseClient.sql(
                        "SELECT product_id, name, description, price, quantity, category, active, created_at, updated_at " +
                                "FROM products ORDER BY product_id"
                ).map((row, metadata) -> {
                    ProductEntity product = new ProductEntity();
                    product.setProductId(row.get("product_id", Long.class));
                    product.setName(row.get("name", String.class));
                    product.setDescription(row.get("description", String.class));

                    Double price = row.get("price", Double.class);
                    product.setPrice(price != null ? price : 0.0);

                    Integer quantity = row.get("quantity", Integer.class);
                    product.setQuantity(quantity != null ? quantity : 0);

                    product.setCategory(row.get("category", String.class));
                    product.setActive(row.get("active", Boolean.class));
                    product.setCreatedAt(row.get("created_at", LocalDateTime.class));
                    product.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
                    return product;
                })
                .all()
                .doOnComplete(() -> log.info("All products fetched"));
    }

    public Mono<ProductEntity> updateProduct(Long productId, ProductEntity productEntity) {
        log.info("Updating product with ID: {}", productId);

        return getProductById(productId)
                .flatMap(existingProduct -> databaseClient.sql(
                                "UPDATE products SET name = :name, description = :description, " +
                                        "price = :price, quantity = :quantity, category = :category, active = :active, " +
                                        "updated_at = :updatedAt WHERE product_id = :productId " +
                                        "RETURNING product_id, name, description, price, quantity, category, active, created_at, updated_at"
                        )
                        .bind("name", productEntity.getName())
                        .bind("description", productEntity.getDescription())
                        .bind("price", productEntity.getPrice())
                        .bind("quantity", productEntity.getQuantity())
                        .bind("category", productEntity.getCategory())
                        .bind("active", productEntity.getActive() != null ? productEntity.getActive() : true)
                        .bind("updatedAt", LocalDateTime.now())
                        .bind("productId", productId)
                        .map((row, metadata) -> {
                            ProductEntity product = new ProductEntity();
                            product.setProductId(row.get("product_id", Long.class));
                            product.setName(row.get("name", String.class));
                            product.setDescription(row.get("description", String.class));

                            Double price = row.get("price", Double.class);
                            product.setPrice(price != null ? price : 0.0);

                            Integer quantity = row.get("quantity", Integer.class);
                            product.setQuantity(quantity != null ? quantity : 0);

                            product.setCategory(row.get("category", String.class));
                            product.setActive(row.get("active", Boolean.class));
                            product.setCreatedAt(row.get("created_at", LocalDateTime.class));
                            product.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
                            return product;
                        })
                        .one()
                        .doOnSuccess(entity -> log.info("product updated with product name: {}", entity.getName())));
    }

    public Mono<Long> deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);
        return databaseClient.sql("DELETE FROM products WHERE product_id = :productId")
                .bind("productId", productId)
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf)
                .doOnSuccess(rows -> {
                    if (rows > 0) {
                        log.info("product deleted successfully with ID: {}", productId);
                    } else {
                        log.warn("No product found with ID: {}", productId);
                    }
                });
    }

}
